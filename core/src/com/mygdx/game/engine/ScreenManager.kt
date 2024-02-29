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
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.utils.InputProcessorHudFirst
import com.mygdx.game.engine.utils.InputProcessorTee
import com.mygdx.game.engine.utils.InputProcessorTranslator
import kotlin.reflect.KClass

object EmptyScreen: Screen()

class ScreenManager(
    namespace: String,
    private val width: Float = Config.WINDOW_WIDTH.toFloat(),
    private val height: Float = Config.WINDOW_HEIGHT.toFloat()
) {
    val spriteSheetManager: SpriteSheetManager = SpriteSheetManager(namespace)

    private val worldUnprojectBuffer = Vector3.Zero
    private val worldUnprojectResult = Vector2.Zero

    private var worldCamera: OrthographicCamera? = null
    private var worldViewport: Viewport? = null

    private var hudCamera: OrthographicCamera? = null
    private var hudViewport: Viewport? = null

    private val hudUnprojectBuffer = Vector3.Zero
    private val hudUnprojectResult = Vector2.Zero

    private var batch: SpriteBatch? = null
    private val screens = mutableMapOf<KClass<*>, Screen>()
    private var currentScreen: Screen? = null

    private val worldInputProcessorTee = InputProcessorTee()
    private val hudInputProcessorTee = InputProcessorTee()

    private val swapper = SceneSwapper(
        { screen -> screen.unload() },
        { screen -> screen.load() },
        { screen -> currentScreen = screen }
    )

    private val dimmer = ScreenDimmer()

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

    fun startup(bootScreen: KClass<*>) {

        worldCamera = OrthographicCamera()
        worldViewport = ExtendViewport(1600f, 1050f, 1920f, 1200f, worldCamera)
        worldViewport!!.update(width.toInt(), height.toInt(), true)

        worldCamera!!.translate(25f, 75f ,0f)
        worldCamera!!.update()

        hudCamera = OrthographicCamera()
        hudViewport = ExtendViewport(1600f, 1050f, 1920f, 1200f, hudCamera)
        hudViewport!!.update(width.toInt(), height.toInt(), true)

        hudCamera!!.translate(25f, 75f ,0f)
        hudCamera!!.update()



        spriteSheetManager.init()

        batch = SpriteBatch()

        swapper.loadContent()
        dimmer.create()

        currentScreen = EmptyScreen
        currentScreen!!.load()

        switch(bootScreen)
    }

    fun shutdown() {
        currentScreen!!.unload()
        dimmer.destroy()
        batch!!.dispose()
    }

    fun resize(windowWidth: Int, windowHeight: Int) {
        worldViewport!!.update(windowWidth, windowHeight)
        hudViewport!!.update(windowWidth, windowHeight)
    }

    fun register(gameScreens: List<Screen>) {
        gameScreens.forEach {
            it.wireScreenManager(this)
            screens[it::class] = it
        }
    }

    fun switch(screen: KClass<*>) {
        Gdx.input.inputProcessor = inputProcessor
        clearInputProcessors()
        swapper.swap(
            currentScreen,
            screens[screen]!!
        )
    }

    fun update(dt: Float) {
        handleInput()
        swapper.update(dt)
        dimmer.update(dt)
        currentScreen!!.updateContents(dt)
    }

    fun render() {
        val clearColor = currentScreen!!.clearColor
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderScreen()
        renderOverlay()
    }

    private fun renderScreen() = with(batch!!) {
        worldViewport!!.apply()
        val worldProjectionMatrix = worldViewport!!.camera.combined
        this.projectionMatrix = worldProjectionMatrix

        begin()
        currentScreen!!.renderWorldComplete(this)
        end()

        hudViewport!!.apply()
        val hudProjectionMatrix = hudViewport!!.camera.combined
        this.projectionMatrix = hudProjectionMatrix

        begin()
        dimmer.render(this)
        currentScreen!!.renderOverlayComplete(this)
        end()

        begin()
        currentScreen!!.renderGlobalOverlay(this)
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

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            Gdx.graphics.setWindowedMode(1920/2, 1080/2)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            Gdx.graphics.setWindowedMode(1440/2, 1080/2)
        }

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

    fun dimScene(amount: Float, speed: ScreenDimmer.DimSpeed, onDone: () -> Unit = {}) =
        dimmer.apply(amount, speed, onDone)
}