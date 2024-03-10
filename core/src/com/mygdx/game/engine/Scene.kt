package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.stdx.GameObject
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
                //scene.sceneManager!!.defaultShot.cut()
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

class CameraController(
    private val scene: Scene
) {
    val camera = scene.sceneManager.sceneCamera
}

abstract class Scene(
    val name: String,
    val sceneManager: SceneManager,
    val clearColor: Color = Color.BLACK,
    val width: Int = Config.WINDOW_WIDTH,
    val height: Int = Config.WINDOW_HEIGHT,
): GameObject("Scene - $name") {

    internal var leavingScene = false
    internal val cinematicBars = CinematicBars()
    protected var initialShot: Shot = sceneManager.defaultShot()

    internal val defaultPanel = panelOf(
        caption = "Default scene panel",
        left = 0f,
        bottom = 0f,
        dimension = Engine.canvas.surface.width, type = PanelDimension.WIDTH
    )

    final override val managedContent = mutableListOf(
        managedContentOf(
            contentIdentifier = "Setup",
            load = {
                leavingScene = false
            },
            unload = { }
        ),
        cinematicBars,
        initialShot,
        defaultPanel
    )

    protected fun manageContent(vararg content: ManagedContent) {
        managedContent.addAll(content)
    }

    /*StaticShot(
        allocator = localAllocator,
        caption = "Default Scene Shot",
        factory = shotFactory,
        scene = this,
        left = 0f,
        bottom = 0f,
        dimension = Engine.canvas.surface.width,
        type = ShotDimension.WIDTH,
        duration = 10f
    )*/







    protected val sheets: () -> SpriteSheetManager = {
        sceneManager!!.spriteSheetManager
    }

    protected val shotFactory: Deferred<ShotFactory>
        get() { return deferred { sceneManager!!.shotFactory } }

    protected fun gameSceneComposerOf(
        composerName: String,
        cameraController: CameraController,
        initialTimeline: GameScene,
        others: List<GameScene>
    ) = GameSceneComposer(
        parent = this,
        camera = cameraController.camera,
        timelineName = composerName,
        initial = initialTimeline,
        others = others,
        diagnosticsPanel = sceneManager!!.diagnostics
    )

    override fun update(dt: Float) {
        initialShot.update(dt)
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

    protected open fun overlay(batch: SpriteBatch) { }

    fun renderWorldComplete(batch: SpriteBatch) {
        render(batch)
        initialShot.render(batch)
    }

    fun renderOverlayComplete(batch: SpriteBatch) {
        overlay(batch)
    }

    fun renderGlobalOverlay(batch: SpriteBatch) {
        cinematicBars.render(batch)
    }

}