package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import kotlin.reflect.KClass

enum class LayerType {
    WORLD,
    HUD
}

abstract class Screen(
    protected val width: Int = Config.WINDOW_WIDTH,
    protected val height: Int = Config.WINDOW_HEIGHT
) {
    private var screenManager: ScreenManager? = null
    private var camera: Camera? = null
    private var batch: SpriteBatch? = null
    private var debugRenderer = DebugRenderer(Config.DEBUG_RENDER_SCREEN_BOUNDS)

    private var loadingAction: StepAction? = null
    private var unloadingAction: StepAction? = null

    private var leavingScreen = false

    val hasUnloaded: Boolean
        get() = unloadingAction != null && unloadingAction!!.hasFinished

    val hasLoaded: Boolean
        get() = loadingAction != null && loadingAction!!.hasFinished

    protected abstract fun update(dt: Float)

    fun updateContents(dt: Float) {
        if (loadingAction != null) {
            loadingAction!!.step()
        }
        if (unloadingAction != null) {
            unloadingAction!!.step()
        }
        update(dt)
    }

    fun registerInput(layer: LayerType, hotspot: Hotspot) {
        screenManager!!.addInputProcessor(layer, hotspot)
    }

    protected fun <T: Screen> switch(screen: KClass<T>) {
        if (!leavingScreen) {
            leavingScreen = true
            screenManager!!.switch(screen)
        }
    }

    fun wireScreenManager(screenManager: ScreenManager) {
        this.screenManager = screenManager
    }

    protected abstract fun loadContents(): StepAction
    protected abstract fun unloadContents(): StepAction
    protected abstract fun renderWorld(batch: SpriteBatch)
    protected abstract fun renderHud(batch: SpriteBatch)

    fun load() {
        camera = OrthographicCamera(width.toFloat(), height.toFloat())
        camera!!.position.set(camera!!.viewportWidth / 2f, camera!!.viewportHeight / 2f, 0f)
        batch = SpriteBatch()
        leavingScreen = false
        loadingAction = loadContents()
        loadingAction!!.start()
    }

    fun unload() {
        unloadingAction = unloadContents()
        unloadingAction!!.start()
        batch!!.dispose()
    }

    fun renderWorldComplete(batch: SpriteBatch) {
        renderWorld(batch!!)
        batch!!.end()
       /* debugRenderer.drawRect(
            batch!!.projectionMatrix,
            160f, 75f,
            1755f, 1050f
        )*/
        batch!!.begin()
    }

    fun renderHudComplete(batch: SpriteBatch) {
        renderHud(batch!!)
        batch!!.end()
     /*   debugRenderer.drawRect(
            batch!!.projectionMatrix,
            1f, 1f,
            1f, height.toFloat(),
            width.toFloat(), height.toFloat(),
            width.toFloat(), 1f
        )*/
        batch!!.begin()
    }

}