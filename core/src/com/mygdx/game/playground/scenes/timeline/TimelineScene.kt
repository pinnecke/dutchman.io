package com.mygdx.game.playground.scenes.timeline

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene
import com.mygdx.game.playground.scenes.timeline.scenes.GameScene1
import com.mygdx.game.playground.scenes.timeline.scenes.GameScene2
import kotlin.reflect.KClass

class TimelineScene(sceneManager: SceneManager): Scene(
    "Timeline Scene",
    sceneManager = sceneManager,
    clearColor = Color.GRAY,
) {
    private val sequence = SequenceController(this)
    private val cameraController = CameraController(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val instructionHint = Label(
        "[1] Game Scene 1\n" +
             "[2] Game Scene 2\n" +
             "[SPACE] start/pause/resume timeline \n" +
             "[S] stop \n" +
             "[BACKSPACE] rewind \n" +
             "[ESC] back\n\n",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private val selectionHint = Label(
        "Game Scene 1",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 260f,
    )

    override val composer = composerOf(
        composerName = "Timeline Scene",
        cameraController = cameraController,
        initialTimeline = GameScene1(),
        others = listOf(
            GameScene2()
        )
    )

    private var selection: KClass<*> = GameScene1::class

    init {
        manageContent(
            instructionHint,
            selectionHint,
            composer
        )

        input[Input.Keys.NUM_1] = {
            selectionHint.text = "Game Scene 1"
            selection = GameScene1::class
        }
        input[Input.Keys.NUM_2] = {
            selectionHint.text = "Game Scene 2"
            selection = GameScene2::class
        }
        input[Input.Keys.SPACE] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(selection)
                } else if (composer.isRunning && composer.isPaused) {
                    composer.resume()
                } else if (composer.isRunning && !composer.isPaused) {
                    composer.pause()
                }
            }
        }
        input[Input.Keys.S] = {
            composer.stop()
        }
        input[Input.Keys.BACKSPACE] = {
            composer.rewind()
        }

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }

        selectionHint.visible = true
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
        selectionHint.render(batch)
        instructionHint.render(batch)
    }

}

