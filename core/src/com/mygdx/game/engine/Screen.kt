package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.engine.objects.Rectangle
import com.mygdx.game.engine.sprites.SpriteSheetManager
import kotlin.reflect.KClass

enum class LayerType {
    WORLD,
    HUD
}

abstract class Screen(
    backPanelColor: Color = Color.BLACK,
    val clearColor: Color = Color.BLACK,
    val width: Int = Config.WINDOW_WIDTH,
    val height: Int = Config.WINDOW_HEIGHT,
) {
    private var screenManager: ScreenManager? = null

    private var leavingScreen = false

    private val cinematicBars = CinematicBars()

    protected val sheets: () -> SpriteSheetManager = {
        screenManager!!.spriteSheetManager
    }

    private val backPanel = Rectangle(
        Engine.canvas.surface.left, Engine.canvas.surface.bottom,
        Engine.canvas.surface.width, Engine.canvas.surface.height,
        backPanelColor
    )

    protected open fun update(dt: Float) { }

    protected fun screenToOverlay(x: Float, y: Float): Vector2 {
        val desktop = screenManager!!.projectWorld(x, y)
        val dx = desktop.x
        val dy = desktop.y
        return screenManager!!.unprojectHud(dx, dy)
    }

    protected fun thisScreen(): Screen = this

    // ----------------------------------------------------------------------------------
    // Scene API
    // ----------------------------------------------------------------------------------

    protected fun <T: Screen> enterScene(screen: KClass<T>) {
        if (!leavingScreen) {
            leavingScreen = true
            screenManager!!.switch(screen)
        }
    }

    protected fun dimScene(
        amount: Float,
        speed: ScreenDimmer.DimSpeed = ScreenDimmer.DimSpeed.MEDIUM
    ) {
        screenManager!!.dimScene(amount, speed, { println("Done dimming") })
    }

    protected fun cinematicModeOn(
        onCinematicModeStarted: () -> Unit = { }
    ) {
        cinematicBars.show()
        onCinematicModeStarted()
    }

    protected fun cinematicModeOff(
        onCinematicModeExits: () -> Unit = { }
    ) {
        cinematicBars.hide()
        onCinematicModeExits()
    }

    protected fun isInCinematicMode() = cinematicBars.visible()

    // ----------------------------------------------------------------------------------

    fun updateContents(dt: Float) {
        cinematicBars.update(dt)
        update(dt)
    }

    fun registerInput(layer: LayerType, hotspot: Hotspot) {
        screenManager!!.addInputProcessor(layer, hotspot)
    }


    fun wireScreenManager(screenManager: ScreenManager) {
        this.screenManager = screenManager
    }

    protected open fun loadContents() { }
    protected open fun unloadContents() { }
    protected open fun render(batch: SpriteBatch) { }
    protected open fun renderOverlay(batch: SpriteBatch) { }

    fun load() {
        backPanel.create()
        cinematicBars.create()
        leavingScreen = false
        loadContents()
    }

    fun unload() {
        unloadContents()
        cinematicBars.destroy()
        backPanel.destroy()
    }

    fun renderWorldComplete(batch: SpriteBatch) {
        backPanel.render(batch)
        render(batch)
    }

    fun renderOverlayComplete(batch: SpriteBatch) {
        renderOverlay(batch)
    }

    fun renderGlobalOverlay(batch: SpriteBatch) {
        cinematicBars.render(batch)
    }

}