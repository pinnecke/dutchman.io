package com.mygdx.game.playground.scenes.timeline

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene
import com.mygdx.game.playground.scenes.timeline.scenes.StaticShotCameraMovement
import kotlin.reflect.KClass

class CameraMovementStaticShotScene(sceneManager: SceneManager): Scene(
    "Camera Movement Static Shot",
    sceneManager = sceneManager,
    clearColor = Color.GRAY,
    defaultPanelCaption = "Creator panel",
    defaultPanelLeft = -1000f,
    defaultPanelBottom = -800f,
    defaultPanelDimension = 4000f,
    defaultPanelType = PanelDimension.WIDTH
) {
    private val sequence = SequenceController(this)
    private val cameraController = CameraController(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val instructionHint = Label(
        "[1] Start\n" +
             "[ESC] back\n\n",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private val composer = gameSceneComposerOf(
        composerName = "Timeline Scene",
        cameraController = cameraController,
        autoStart = false,
        initialTimeline = StaticShotCameraMovement(
            this,
            cameraController,
            this.defaultPanel,
            instructionHint
        ),
        others = listOf(

        )
    )

    private var selection: KClass<*> = StaticShotCameraMovement::class

    init {
        manageContent(
            instructionHint,
            composer
        )

        input[Input.Keys.NUM_1] = {
            selection = StaticShotCameraMovement::class
        }
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

