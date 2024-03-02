package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.engine.sprites.SpriteSheetManager
import kotlin.reflect.KClass

enum class LayerType {
    WORLD,
    HUD
}

abstract class Scene(
    val clearColor: Color = Color.BLACK,
    val width: Int = Config.WINDOW_WIDTH,
    val height: Int = Config.WINDOW_HEIGHT,
) {
    private var sceneManager: SceneManager? = null

    private var leavingScene = false

    private val cinematicBars = CinematicBars()

    protected val sheets: () -> SpriteSheetManager = {
        sceneManager!!.spriteSheetManager
    }

    protected open fun update(dt: Float) { }

    protected fun sceneToOverlay(x: Float, y: Float): Vector2 {
        val desktop = sceneManager!!.projectWorld(x, y)
        val dx = desktop.x
        val dy = desktop.y
        return sceneManager!!.unprojectHud(dx, dy)
    }

    protected fun self(): Scene = this

    // ----------------------------------------------------------------------------------
    // Scene API
    // ----------------------------------------------------------------------------------

    protected fun <T: Scene> enterScene(scene: KClass<T>) {
        if (!leavingScene) {
            leavingScene = true
            cinematicModeOff()
            sceneManager!!.switch(
                scene
            ) {
                dimScene(0.0f, SceneDimmer.DimSpeed.HIGH)
            }
        }
    }

    protected fun dimScene(
        amount: Float,
        speed: SceneDimmer.DimSpeed = SceneDimmer.DimSpeed.MEDIUM
    ) {
        sceneManager!!.dimScene(amount, speed) { println("Done dimming") }
    }

    protected fun cinematicModeOn(
        onDone: () -> Unit = { }
    ) {
        if (cinematicBars.isAbsent()) {
            cinematicBars.show()
            onDone()
        }
    }

    protected fun cinematicModeOff(
        onDone: () -> Unit = { }
    ) {
        if (cinematicBars.isPresent()) {
            cinematicBars.hide()
            onDone()
        }
    }

    protected fun isInCinematicMode() = cinematicBars.isPresent()

    // ----------------------------------------------------------------------------------

    fun updateContents(dt: Float) {
        cinematicBars.update(dt)
        update(dt)
    }

    fun registerInput(layer: LayerType, hotspot: Hotspot) {
        sceneManager!!.addInputProcessor(layer, hotspot)
    }


    fun injectSceneManager(sceneManager: SceneManager) {
        this.sceneManager = sceneManager
    }

    protected open fun create() { }
    protected open fun destroy() { }
    protected open fun render(batch: SpriteBatch) { }
    protected open fun renderOverlay(batch: SpriteBatch) { }

    fun load() {
        cinematicBars.create()
        leavingScene = false
        create()
    }

    fun unload() {
        destroy()
        cinematicBars.destroy()
    }

    fun renderWorldComplete(batch: SpriteBatch) {
        render(batch)
    }

    fun renderOverlayComplete(batch: SpriteBatch) {
        renderOverlay(batch)
    }

    fun renderGlobalOverlay(batch: SpriteBatch) {
        cinematicBars.render(batch)
    }

}