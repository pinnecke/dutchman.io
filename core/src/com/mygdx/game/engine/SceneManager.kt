package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.DiagnosticsPanel
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.shots.StaticShot
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.utils.InputProcessorHudFirst
import com.mygdx.game.engine.utils.InputProcessorTee
import com.mygdx.game.engine.utils.InputProcessorTranslator
import com.mygdx.game.engine.utils.deferred
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass


fun emptyScene(sceneManager: SceneManager) = object: Scene("Empty Scene", sceneManager) {

}

class SceneManager(
    namespace: String,
    private val bootScene: KClass<*>,
    private val width: Float = Config.WINDOW_WIDTH.toFloat(),
    private val height: Float = Config.WINDOW_HEIGHT.toFloat(),
): GameObject("Scene Manager - $namespace") {

    private var batch: SpriteBatch? = null
    private val renderBuffer = RenderBuffer("screen renderer")

    internal var worldCamera: OrthographicCamera? = null
    private var worldViewport: Viewport? = null
    private var wordCameraDeltaStore: CameraDeltaStore? = null

    private var hudCamera: OrthographicCamera? = null
    private var hudViewport: Viewport? = null

    val spriteSheetManager: SpriteSheetManager = SpriteSheetManager(namespace)

    var sceneTransition = SceneTransition()

    private val emptyScene = emptyScene(this)

    private val dimmer = SceneDimmer()

    internal val diagnostics = DiagnosticsPanel()

    internal val sceneCamera = SceneCamera()

    private val swapper = SceneSwapper(
        loadingScene = emptyScene,
        { scene -> scene.unloadContent() },
        { scene -> scene.loadContent() },
        { scene ->
            sceneCamera.hardCut(scene.defaultPanel)
            currentScene = scene
        },
    )

    override val managedContent = mutableListOf(
        managedContentOf(
            contentIdentifier = "Sprite Batch",
            load = {
                batch = SpriteBatch()
            },
            unload = {
                batch!!.dispose()
            }
        ),
        renderBuffer,
        managedContentOf(
            contentIdentifier = "Camera setup",
            load = {
                worldCamera = OrthographicCamera()
                worldViewport = StretchViewport(1920f, 1200f, worldCamera)
                worldViewport!!.update(width.toInt(), height.toInt(), true)

                worldCamera!!.translate(25f, 75f ,0f)
                worldCamera!!.update()

                sceneTransition.camera = worldCamera

                wordCameraDeltaStore = CameraDeltaStore(worldCamera!!)
                wordCameraDeltaStore!!.loadContent()

                hudCamera = OrthographicCamera()
                hudViewport = ExtendViewport(1600f, 1050f, 1920f, 1200f, hudCamera)
                hudViewport!!.update(width.toInt(), height.toInt(), true)

                hudCamera!!.translate(25f, 75f ,0f)
                hudCamera!!.update()
            },
            unload = { }
        ),
        managedContentOf(
            contentIdentifier = "Sprite Sheet Manager",
            load = {
                spriteSheetManager.init()
            },
            unload = {

            }
        ),
        sceneTransition,
        swapper,
        dimmer,
        emptyScene,
        diagnostics,
        managedContentOf(
            contentIdentifier = "Boot scene setup",
            load = {
                currentScene = emptyScene
                switch(bootScene)
            },
            unload = {
                currentScene!!.unloadContent()
            }
        ),
        managedContentOf(
            contentIdentifier = "Scene Camera setup",
            load = {
                sceneCamera.camera = worldCamera
            },
            unload = { }
        )
    )

    private val worldUnprojectBuffer = Vector3.Zero
    private val worldUnprojectResult = Vector2.Zero

    var shotFactory = ShotFactory(deferred { this })

    private val hudUnprojectBuffer = Vector3.Zero
    private val hudUnprojectResult = Vector2.Zero

    private val scenes = mutableMapOf<KClass<*>, Scene>()
    private var currentScene: Scene? = null

    private val worldInputProcessorTee = InputProcessorTee()
    private val hudInputProcessorTee = InputProcessorTee()

    internal fun defaultShot() = StaticShot(
        caption = "Default Shot",
        factory = deferred { shotFactory },
        left = 0f,
        bottom = 0f,
        dimension = Engine.canvas.surface.width,
        type = ShotDimension.WIDTH,
        debuggable = false,
        duration = Float.POSITIVE_INFINITY
    )

    private val inputProcessor = InputProcessorHudFirst(
        InputProcessorTranslator(::unprojectHud, hudInputProcessorTee),
        InputProcessorTranslator(::unprojectWorld, worldInputProcessorTee)
    )





    fun addInputProcessor(layer: LayerType, inputProcessor: InputProcessor) =
        when (layer) {
            LayerType.WORLD -> worldInputProcessorTee.add(inputProcessor)
            LayerType.HUD -> hudInputProcessorTee.add(inputProcessor)
        }

    fun unprojectWorld(x: Float, y: Float): Vector2 {
        worldUnprojectBuffer.set(x, y, 0f)
        worldViewport!!.unproject(worldUnprojectBuffer)
        worldUnprojectResult.set(worldUnprojectBuffer.x, worldUnprojectBuffer.y)
        return worldUnprojectResult
    }

    fun projectWorld(x: Float, y: Float): Vector2 {
        worldUnprojectBuffer.set(x, y, 0f)
        worldViewport!!.project(worldUnprojectBuffer)
        worldUnprojectResult.set(worldUnprojectBuffer.x, worldUnprojectBuffer.y)
        return worldUnprojectResult
    }

    fun unprojectHud(x: Float, y: Float): Vector2 {
        hudUnprojectBuffer.set(x, y, 0f)
        hudViewport!!.unproject(hudUnprojectBuffer)
        hudUnprojectResult.set(hudUnprojectBuffer.x, hudViewport!!.worldHeight - hudUnprojectBuffer.y)
        return hudUnprojectResult
    }

    fun resize(windowWidth: Int, windowHeight: Int) {
        worldViewport!!.update(windowWidth, windowHeight)
        hudViewport!!.update(windowWidth, windowHeight)
        renderBuffer.resize(windowWidth, windowHeight)
    }

    fun register(gameScenes: List<Scene>) {
        gameScenes.forEach {
            scenes[it::class] = it
        }
    }

    fun switch(scene: KClass<*>, beforeNextScene: () -> Unit = { }) {
        Gdx.input.inputProcessor = inputProcessor
        clearInputProcessors()
        swapper.swap(
            currentScene,
            scenes[scene]!!,
            beforeNextScene
        )
    }

    override fun update(dt: Float) {
        handleInput()
        swapper.update(dt)
        dimmer.update(dt)
        //defaultShot.update(dt)
        sceneTransition.update(dt)
        shotFactory.update(dt)
        diagnostics.update(dt)
        sceneCamera.update(dt)
        currentScene!!.updateContents(dt)

        worldViewport!!.apply()
        wordCameraDeltaStore?.update()
    }

    override fun render(batch: SpriteBatch) {
        throw NotImplementedError("Use #render() method instead")
    }

    fun render() {
        renderScene()
        renderOverlay()
    }


    private fun renderIntoBuffer() = with(batch!!) {
        worldViewport!!.apply()
        val worldProjectionMatrix = worldViewport!!.camera.combined
        this.projectionMatrix = worldProjectionMatrix

        renderBuffer.renderIntoBuffer {
            val clearColor = currentScene!!.clearColor
            Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            begin()
            currentScene!!.renderWorldComplete(this)
            end()
        }
    }

    private fun renderToScreen() {
        renderBuffer.renderOntoScreen { screen, content ->
            val delta = wordCameraDeltaStore!!.delta
            val span = delta.span
            if (span < 80) {
                screen.setColor(screen.color.r, screen.color.g, screen.color.b, 1.0f)
                screen.draw(
                    content,
                    0f,
                    0f,
                    Engine.canvas.surface.width,
                    Engine.canvas.surface.height
                )
            } else {
                println("BLUR")
                val steps = 10
                for (i in steps downTo 1) {
                    val alpha = i / steps.toFloat()
                    screen.setColor(screen.color.r, screen.color.g, screen.color.b, alpha)
                    screen.draw(
                        content,
                        alpha * delta.x,
                        alpha * delta.y,
                        Engine.canvas.surface.width,
                        Engine.canvas.surface.height
                    )
                }
            }
        }
    }

    private fun renderScene() = with(batch!!) {

        renderIntoBuffer()
        renderToScreen()

        hudViewport!!.apply()
        val hudProjectionMatrix = hudViewport!!.camera.combined
        this.projectionMatrix = hudProjectionMatrix

        begin()
        dimmer.render(this)
        currentScene!!.renderOverlayComplete(this)
        end()

        begin()
        currentScene!!.renderGlobalOverlay(this)
        diagnostics.render(this)
        end()
    }

    private fun renderOverlay() {
        swapper.render()
    }

    private fun clearInputProcessors() {
        worldInputProcessorTee.clear()
    }


    private fun handleInput() { // TODO: Remove
        worldViewport!!.apply()

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            worldCamera!!.zoom += 0.005f
            println("${worldCamera!!.zoom}")
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            worldCamera!!.zoom -= 0.005f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            worldCamera!!.translate(-3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            worldCamera!!.translate(3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            worldCamera!!.translate(0f, -3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            worldCamera!!.translate(0f, 3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            worldCamera!!.rotate(-0.2f, 0f, 0f, 1f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            worldCamera!!.rotate(0.2f, 0f, 0f, 1f)
        }

        worldCamera!!.update()

        //camera!!.zoom = MathUtils.clamp(camera!!.zoom, 0.1f, 100 / camera!!.viewportWidth)
        //val effectiveViewportWidth: Float = camera!!.viewportWidth * camera!!.zoom
        //val effectiveViewportHeight: Float = camera!!.viewportHeight * camera!!.zoom
        //camera!!.position.x = MathUtils.clamp(camera!!.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f)
        //camera!!.position.y =
        //    MathUtils.clamp(camera!!.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f)
    }

    fun dimScene(amount: Float, speed: SceneDimmer.DimSpeed, onDone: () -> Unit = {}) =
        dimmer.apply(amount, speed, onDone)
}