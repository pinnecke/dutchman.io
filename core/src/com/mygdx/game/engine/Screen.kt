package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.engine.sprites.SpriteSheetManager
import kotlin.reflect.KClass

enum class LayerType {
    WORLD,
    HUD
}

abstract class Screen(
    val backgroundColor: Color = Color.BLACK,
    val width: Int = Config.WINDOW_WIDTH,
    val height: Int = Config.WINDOW_HEIGHT
) {
    private var screenManager: ScreenManager? = null

    private var loadingAction: StepAction? = null
    private var unloadingAction: StepAction? = null

    private var leavingScreen = false

    private val cinematicBars = CinematicBars()

    protected val sheets: () -> SpriteSheetManager = {
        screenManager!!.spriteSheetManager
    }

    val hasUnloaded: Boolean
        get() = unloadingAction != null && unloadingAction!!.hasFinished

    val hasLoaded: Boolean
        get() = loadingAction != null && loadingAction!!.hasFinished

    protected abstract fun update(dt: Float)

    protected fun screenToOverlay(x: Float, y: Float): Vector2 {
        val desktop = screenManager!!.projectWorld(x, y)
        val dx = desktop.x
        val dy = desktop.y
        return screenManager!!.unprojectHud(dx, dy)
    }

    protected fun thisScreen(): Screen = this

    protected fun enterCinematicMode(
        onCinematicModeStarted: () -> Unit = { }
    ) {
        cinematicBars.show()
        onCinematicModeStarted()
    }

    protected fun exitCinematicMode(
        onCinematicModeExits: () -> Unit = { }
    ) {
        cinematicBars.hide()
        onCinematicModeExits()
    }

    protected fun isInCinematicMode() = cinematicBars.visible()

    fun updateContents(dt: Float) {
        if (loadingAction != null && !loadingAction!!.hasFinished) {
            loadingAction!!.step()
        }
        if (unloadingAction != null && !unloadingAction!!.hasFinished) {
            unloadingAction!!.step()
        }
        cinematicBars.update(dt)
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
        cinematicBars.load()
        leavingScreen = false
        loadingAction = loadContents()
        loadingAction!!.start()
    }

    fun unload() {
        unloadingAction = unloadContents()
        unloadingAction!!.start()
    }

    fun renderWorldComplete(batch: SpriteBatch) {
        renderWorld(batch)
    }

    fun renderHudComplete(batch: SpriteBatch) {
        renderHud(batch)
    }

    fun renderOverlay(batch: SpriteBatch) {
        cinematicBars.render(batch)
    }

}