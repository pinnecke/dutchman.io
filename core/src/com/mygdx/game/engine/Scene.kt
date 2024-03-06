package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.utils.Deferred
import com.mygdx.game.engine.utils.deferred
import kotlin.reflect.KClass

enum class LayerType {
    WORLD,
    HUD
}

class SequenceController(
    private val scene: Scene
) {
    fun <T: Scene> switch(otherScene: KClass<T>) {
        if (!scene.leavingScene) {
            scene.leavingScene = true
            scene.cinematicBars.hide()
            scene.sceneManager!!.switch(
                otherScene
            ) {
                scene.sceneManager!!.dimScene(0.0f, SceneDimmer.DimSpeed.HIGH) {  }
                scene.sceneManager!!.defaultShot.apply()
            }
        }
    }
}

class SceneController(
    private val scene: Scene
) {


    fun cut(frame: Panel) {
        scene.sceneManager!!.sceneTransition.cut(frame)
    }

    fun dimScene(
        amount: Float,
        speed: SceneDimmer.DimSpeed = SceneDimmer.DimSpeed.MEDIUM
    ) {
        scene.sceneManager!!.dimScene(amount, speed) { println("Done dimming") }
    }

    fun cinematicModeOn(
        onDone: () -> Unit = { }
    ) {
        if (scene.cinematicBars.isAbsent()) {
            scene.cinematicBars.show()
            onDone()
        }
    }

    fun cinematicModeOff(
        onDone: () -> Unit = { }
    ) {
        if (scene.cinematicBars.isPresent()) {
            scene.cinematicBars.hide()
            onDone()
        }
    }

    fun isInCinematicMode() = scene.cinematicBars.isPresent()
}

abstract class Scene(
    val clearColor: Color = Color.BLACK,
    val width: Int = Config.WINDOW_WIDTH,
    val height: Int = Config.WINDOW_HEIGHT,
) {
    private val defaultShot = StaticShot(
        caption = "Default Shot",
        factory = shotFactory,
        left = 0f,
        bottom = 0f,
        dimension = Engine.canvas.surface.width,
        type = ShotDimension.WIDTH
    )

    protected var initialShot: Shot = defaultShot

    internal var sceneManager: SceneManager? = null

    internal var leavingScene = false

    internal val cinematicBars = CinematicBars()

    protected val sheets: () -> SpriteSheetManager = {
        sceneManager!!.spriteSheetManager
    }

    protected val shotFactory: Deferred<ShotFactory>
        get() { return deferred { sceneManager!!.shotFactory } }

    protected open fun update(dt: Float) {
        if (sceneManager != null) {
            initialShot.update(dt)
        }
    }

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
    protected open fun overlay(batch: SpriteBatch) { }

    fun load() {
        cinematicBars.create()
        leavingScene = false
        create()

        if (sceneManager != null) {
            if (initialShot == defaultShot) {
                initialShot.create()
            }
            initialShot.apply()
        }
    }

    fun unload() {
        destroy()
        cinematicBars.destroy()

        if (sceneManager != null && initialShot == defaultShot) {
            initialShot.destroy()
        }
    }

    fun renderWorldComplete(batch: SpriteBatch) {
        render(batch)

        if (sceneManager != null) {
            initialShot.render(batch)
        }
    }

    fun renderOverlayComplete(batch: SpriteBatch) {
        overlay(batch)
    }

    fun renderGlobalOverlay(batch: SpriteBatch) {
        cinematicBars.render(batch)
    }

}