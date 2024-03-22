package com.mygdx.game.playground.scenes.camera.pan

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene
import com.mygdx.game.playground.scenes.timeline.scenes.ZoomShotCameraMovement
import kotlin.reflect.KClass

class CameraMovementZoomShotScene(sceneManager: SceneManager): Scene(
    "Camera Movement Zoom Shot",
    sceneManager = sceneManager,
    clearColor = Color.BLACK,
    defaultPanelCaption = "Creator panel",
    defaultPanelLeft = -1775f,
    defaultPanelBottom = -50f,
    defaultPanelDimension = 8000f,
    defaultPanelType = PanelDimension.WIDTH
) {
    private val sequence = SequenceController(this)
    private val cameraController = CameraController(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val instructionHint = Label(
        "[1] Start\n" +
             "[ESC] back\n\n",
        Color.GRAY,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    override val composer = composerOf(
        composerName = "Timeline Scene",
        cameraController = cameraController,
        autoStart = false,
        initialTimeline = ZoomShotCameraMovement(
            this,
            cameraController,
            this.defaultPanel,
            instructionHint
        ),
        others = listOf(

        )
    )

    private var selection: KClass<*> = ZoomShotCameraMovement::class

    init {
        manageContent(
            instructionHint,
            composer
        )

        input[Input.Keys.SPACE] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(selection)
                }
            }
        }

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        composer.update(dt)
        inputDelayer.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        composer.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        instructionHint.render(batch)
    }

}

