package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.SceneShaker
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
            scene.sceneManager.gameCursorApi.visible = true
            scene.sceneManager.gameCursorApi.hotspot = false
            scene.sceneManager.switch(
                otherScene
            ) {
                with (scene) {
                    sceneManager.dimScene(0.0f, SceneDimmer.DimSpeed.HIGH) {  }
                    sceneManager.scenePostEffects.reset()
                    sceneManager.shaker.reset()
                    hintPanel.reset()
                    composer.stop()
                    composer.rewind()
                }
            }
        }
    }
}

class SceneController(
    private val scene: Scene
) {
    val cursor = scene.sceneManager.gameCursorApi

    fun cut(frame: Panel) {
        scene.sceneManager!!.sceneTransition.cut(frame)
    }

    fun dimScene(
        amount: Float,
        speed: SceneDimmer.DimSpeed = SceneDimmer.DimSpeed.MEDIUM
    ) {
        scene.sceneManager!!.dimScene(amount, speed) { println("Done dimming") }
    }

    val shake: SceneShaker
        get() { return scene.sceneManager.shaker }

    val hintPanel: HintPanel
        get() { return scene.hintPanel }


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

class EffectWrapper(
    private val scene: Scene,
    private val accessEffect: (scenePostEffects: ScenePostEffects) -> Tween<*>
) {
    private val effect: Tween<*>
        get() { return accessEffect(scene.sceneManager!!.scenePostEffects) }

    fun configure(
        amount: Float, duration: Float,
        tweenFunction: TweenFunction = TweenFunction.EASE_IN_OUT,
        onDone: () -> Unit = { }
    ) = effect.start(
        amount, duration, tweenFunction, onDone
    )

    var enabled: Boolean
        get() { return effect.enabled }
        set(value) { effect.enabled = value }

    val amount: Float
        get() { return effect.amount }
}

class PostEffects(
    scene: Scene
) {
    val blur = EffectWrapper(scene) { it.blur }
    val gamma = EffectWrapper(scene) { it.gamma }
    val crt = EffectWrapper(scene) { it.crt }
    val grain = EffectWrapper(scene) { it.grain }
    val white = EffectWrapper(scene) { it.white }
    val vignette = EffectWrapper(scene) { it.vignette }
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
    val defaultPanelCaption: String = "Default scene panel",
    val defaultPanelLeft: Float = 0f,
    val defaultPanelBottom: Float = 0f,
    val defaultPanelDimension: Float = Engine.canvas.surface.width,
    val defaultPanelType: PanelDimension = PanelDimension.WIDTH
): GameObject("Scene - $name") {

    internal var leavingScene = false
    internal val cinematicBars = CinematicBars { sceneManager.gameCursorApi }
    protected var initialShot: Shot = sceneManager.defaultShot()

    internal val defaultPanel = panelOf(
        caption = defaultPanelCaption,
        left = defaultPanelLeft,
        bottom = defaultPanelBottom,
        dimension = defaultPanelDimension, type = defaultPanelType
    )

    open val composer: GameSceneComposer = sceneManager.emptyGameSceneComposer()

    val hintPanel = HintPanel( sceneManager.scenePostEffects, { sceneManager.worldCamera!! } )

    final override val managedContent = mutableListOf(
        managedContentOf(
            id = "Setup",
            load = {
                leavingScene = false
            },
            unload = { }
        ),
        cinematicBars,
        initialShot,
        defaultPanel,
        hintPanel
    )

    protected fun manageContent(vararg content: ManagedContent) {
        managedContent.addAll(content)
    }

    val sheets: () -> SpriteSheetManager = {
        sceneManager!!.spriteSheetManager
    }

    protected val shotFactory: Deferred<ShotFactory>
        get() { return deferred { sceneManager!!.shotFactory } }

    protected fun composerOf(
        composerName: String,
        cameraController: CameraController,
        autoStart: Boolean = true,
        cutOnStart: Boolean = true,
        initialTimeline: GameScene,
        others: List<GameScene>
    ) = GameSceneComposer(
        parentName = this.id,
        camera = cameraController.camera,
        timelineName = composerName,
        initial = initialTimeline,
        others = others,
        autoStart = autoStart,
        cutOnStart = cutOnStart,
        diagnosticsPanel = sceneManager!!.diagnostics
    )

    override fun update(dt: Float) {
        initialShot.update(dt)
        hintPanel.update(dt)
    }

    protected fun sceneToOverlay(x: Float, y: Float): Vector2 {
        val desktop = sceneManager!!.projectWorld(x, y)
        val dx = desktop.x
        val dy = desktop.y
        return sceneManager!!.unprojectHud(dx, dy)
    }

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
        hintPanel.render(batch)
    }

    fun renderGlobalOverlay(batch: SpriteBatch) {
        cinematicBars.render(batch)
    }

}