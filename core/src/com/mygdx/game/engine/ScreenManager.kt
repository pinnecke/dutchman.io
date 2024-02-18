package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.engine.utils.InputProcessorHudFirst
import com.mygdx.game.engine.utils.InputProcessorTee
import com.mygdx.game.engine.utils.InputProcessorTranslator
import com.mygdx.game.game.screens.LoadingScreen
import kotlin.reflect.KClass

object EmptyScreen: Screen() {
    override fun update(dt: Float) {}
    override fun loadContents() = emptyStepAction()
    override fun unloadContents() = emptyStepAction()
    override fun renderWorld(batch: SpriteBatch) {}
    override fun renderHud(batch: SpriteBatch) {}
}

class ScreenManager(
    fetchLoadingScreen: () -> LoadingScreen,
    private val width: Float = Config.WINDOW_WIDTH.toFloat(),
    private val height: Float = Config.WINDOW_HEIGHT.toFloat()
) {
    private val worldUnprojectBuffer = Vector3.Zero
    private val worldUnprojectResult = Vector2.Zero

    private var camera: OrthographicCamera? = null
    private var fitViewport: Viewport? = null

    private val hudUnprojectResult = Vector2.Zero

    private var batch: SpriteBatch? = null
    private val screens = mutableMapOf<KClass<*>, Screen>()
    private var currentScreen: Screen? = null

    private val worldInputProcessorTee = InputProcessorTee()
    private val hudInputProcessorTee = InputProcessorTee()

    private val swapper = SceneSwapper(
        fetchLoadingScreen,
        { screen -> screen.unload() },
        { screen -> screen.load() },
        { screen -> currentScreen = screen }
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
        fitViewport!!.unproject(worldUnprojectBuffer)
        worldUnprojectResult.set(worldUnprojectBuffer.x, worldUnprojectBuffer.y)
        return worldUnprojectResult
    }

    fun unprojectHud(x: Float, y: Float): Vector2 {
        hudUnprojectResult.set(x, y)
        return hudUnprojectResult
    }

    fun <T: Screen> startup(bootScreen: KClass<T>) {

        camera = OrthographicCamera()
        fitViewport = ExtendViewport(1600f, 1050f, 1920f, 1200f, camera)
        fitViewport!!.update(width.toInt(), height.toInt(), true)

        camera!!.translate(25f, 75f ,0f)
        camera!!.update()

        batch = SpriteBatch()

        swapper.loadContent()

        currentScreen = EmptyScreen
        currentScreen!!.load()

        switch(bootScreen)
    }

    fun shutdown() {
        currentScreen!!.unload()
        batch!!.dispose()
    }

    fun resize(windowWidth: Int, windowHeight: Int) {
        fitViewport!!.update(windowWidth, windowHeight)
    }

    fun register(vararg gameScreens: Screen) {
        gameScreens.forEach {
            it.wireScreenManager(this)
            screens[it::class] = it
        }
    }

    fun <T: Screen> switch(screen: KClass<T>) {
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
        currentScreen!!.updateContents(dt)
    }

    fun render() {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.5f, 0.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderScreen()
        renderOverlay()
    }

    private fun renderScreen() {
        fitViewport!!.apply()
        val projectionMatrix = fitViewport!!.camera.combined
        batch!!.projectionMatrix = projectionMatrix

        batch!!.begin()
        currentScreen!!.renderWorldComplete(batch!!)
        currentScreen!!.renderHudComplete(batch!!)
        batch!!.end()
    }

    private fun renderOverlay() {

        //swapper.render(projectionMatrix, fitViewport!!.screenWidth.toFloat(), fitViewport!!.screenHeight.toFloat())
    }

    private fun clearInputProcessors() {
        worldInputProcessorTee.clear()
    }


    private fun handleInput() { // TODO: Remove
        fitViewport!!.apply()

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            Gdx.graphics.setWindowedMode(1920/2, 1080/2)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            Gdx.graphics.setWindowedMode(1440/2, 1080/2)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera!!.zoom += 0.005f
            println("${camera!!.zoom}")
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera!!.zoom -= 0.005f
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera!!.translate(-3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera!!.translate(3f, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera!!.translate(0f, -3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera!!.translate(0f, 3f, 0f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera!!.rotate(-0.2f, 0f, 0f, 1f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera!!.rotate(0.2f, 0f, 0f, 1f)
        }

        camera!!.update()

        //camera!!.zoom = MathUtils.clamp(camera!!.zoom, 0.1f, 100 / camera!!.viewportWidth)
        //val effectiveViewportWidth: Float = camera!!.viewportWidth * camera!!.zoom
        //val effectiveViewportHeight: Float = camera!!.viewportHeight * camera!!.zoom
        //camera!!.position.x = MathUtils.clamp(camera!!.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f)
        //camera!!.position.y =
        //    MathUtils.clamp(camera!!.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f)
    }
}