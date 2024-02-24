package com.mygdx.game.engine.sprites

import com.badlogic.gdx.Gdx
import com.mygdx.game.engine.YAML_MAPPER
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.RuntimeException


fun spriteSheetFromYamlContent(
    yamlContents: String
): SpriteSheetDescriptor = YAML_MAPPER.readValue(
    yamlContents,
    SpriteSheetDescriptor::class.java
)

fun spriteSheetFromYamlFile(
    filePath: String,
    fileReader: (filePath: String) -> String = { Gdx.files.internal(it).readString() }
): SpriteSheetDescriptor = spriteSheetFromYamlContent(
    fileReader(filePath)
)

data class SpriteSheetDescriptor internal constructor(
    val root: String,
    val sprites: List<SpriteDescriptor>
)

data class SpriteDescriptor internal constructor(
    val name: String,
    val frames: Int
)

class SpriteSheetManager(
    private val pool: FramePool = framePool(),
    private val fileExists: (filePath: String) -> Boolean = { Gdx.files.internal(it).exists() },
    private val sheets: MutableList<SpriteSheetDescriptor> = mutableListOf(),
    private val spriteDirectoryName: String = "sprites",
    private val scanSpriteDirectory: (directoryName: String) -> List<String> = ::scanAssetDirectory
) {
    private val spriteFramesIndex: MutableMap<String, List<String>> = mutableMapOf()

    fun init() {
        println("Running asset directory scan for sprites")
        val spriteSheetFiles = scanSpriteDirectory(spriteDirectoryName)
            .filter { it.endsWith(".yaml") }
            .map { "$spriteDirectoryName/$it" }
        spriteSheetFiles.forEach {
            println("  - $it")
        }
        add(
            spriteSheetFiles.map {
                spriteSheetFromYamlFile(it)
            }
        )
    }

    fun add(descriptors: List<SpriteSheetDescriptor>) {
        descriptors.forEach { sheets.add(it) }
        runAnalysis()
        refreshIndex()
    }

    fun get(spriteName: String): List<Frame> {
        if (!spriteName.contains(spriteName)) {
            throw SpriteNotFoundException(spriteName)
        }
        if (!spriteFramesIndex.containsKey(spriteName)) {
            throw SpriteNotFoundInternalError(spriteName)
        }
        return spriteFramesIndex[spriteName]!!.map { filePath ->
            pool[filePath]
        }
    }

    private fun refreshIndex() {
        spriteFramesIndex.clear()
        sheets.map { sheet ->
            sheet.sprites.map { sprite ->
                spriteFramesIndex[sprite.name] = computeFilenames(sprite.frames, sheet.root, sprite.name)
            }
        }
    }

    private fun computeFilenames(numberOfFrames: Int, sheetRoot: String, spriteName: String): List<String> {
        val len = numberOfFrames.toString().length
        return (0 until numberOfFrames).map { frameIndex ->
            "${spriteDirectoryName}/${sheetRoot}/${spriteName}-${frameIndex.toString().padStart(len, '0')}.png"
        }
    }

    private fun runAnalysis() {
        val ambiguousRoots = sheets.map { it.root }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
            .distinct()
        val ambiguousNames = sheets.flatMap { it.sprites }
            .map { it.name }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
            .distinct()
        val notFound = sheets.map { sheet ->
                sheet.sprites.map { sprite ->
                    computeFilenames(sprite.frames, sheet.root, sprite.name)
                }.flatten()
                .filterNot { filePath ->
                    fileExists(filePath)
                }
            }.flatten().distinct()

        if (ambiguousRoots.isNotEmpty() || ambiguousNames.isNotEmpty() || notFound.isNotEmpty()) {
            throw SpriteRegisterCorruptionException(ambiguousRoots, ambiguousNames, notFound)
        }
    }
}

class SpriteRegisterCorruptionException (
    ambiguousRoots: List<String>,
    ambiguousNames: List<String>,
    notFound: List<String>
): RuntimeException (
    "Sprite game data is corrupted or misconfigured: detected \n" +
            "- ${ambiguousRoots.size} ambiguous roots \n" +
            "- ${ambiguousNames.size} ambiguous sprite names \n" +
            "- ${notFound.size} asset files could not be found\n" +
            "\n" +
            "Ambiguous roots: ${ambiguousRoots.joinToString() } \n" +
            "Ambiguous sprite names: ${ambiguousNames.joinToString() } \n" +
            "Asset files not existing: ${notFound.joinToString() } \n"
)

class SpriteNotFoundException (
    spriteName: String
): RuntimeException (
    "No such sprite: $spriteName"
)

class SpriteNotFoundInternalError (
    spriteName: String
): RuntimeException (
    "Sprite name not contained in index: $spriteName"
)

private fun scanAssetDirectory(
    spriteDirectoryName: String
): List<String> =
    getResourceFiles(spriteDirectoryName)

private fun getResourceFiles(path: String): List<String> =
    getResourceAsStream(path).use{
        return if (it == null) {
            emptyList()
        } else {
            BufferedReader(InputStreamReader(it)).readLines()
        }
    }

private fun getResourceAsStream(
    resource: String
): InputStream? =
    Thread.currentThread().contextClassLoader.getResourceAsStream(resource)
        ?: resource::class.java.getResourceAsStream(resource)