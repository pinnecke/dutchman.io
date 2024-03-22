package com.mygdx.game.engine.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.Pool
import com.mygdx.game.engine.stdx.Rc

typealias FilePath = String
typealias Frame = Rc<Texture, FilePath>
typealias FramePool = Pool<Texture, FilePath, Frame>

class FrameList(
    override val id: String,
    private val sprite: String,
    private val sheets: () -> SpriteSheetManager
): ManagedContent {

    private var frames: List<Frame> = listOf()

    val first: Frame
        get() { return frames[0] }

    val size: Int
        get() { return frames.size }

    override fun loadContent() {
        frames = sheets().get(sprite)
    }

    override fun unloadContent() {
        frames.forEach {
            it.release()
        }
    }

    operator fun get(index: Int): Frame = frames[index]

}

fun framePool(
    loadTexture: (filePath: String) -> Texture = {
        val texture = Texture(Gdx.files.internal(it), true)
        texture.setFilter(
            TextureFilter.MipMapLinearNearest,
            TextureFilter.Linear
        )
        texture
    }
) = FramePool(
    fetch = loadTexture,
    construct = { filePath, texture, free ->
        Frame(filePath, texture, free)
    },
    free = { it.value.dispose() }
)