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

class TimelineScene(sceneManager: SceneManager): Scene(
    "Timeline Scene",
    sceneManager = sceneManager,
    clearColor = Color.GRAY,
) {
    private val sequence = SequenceController(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val instructionHint = Label(
        "[1] Timeline 1\n" +
             "[2] Timeline 2\n" +
             "[SPACE] start/pause/resume \n" +
             "[S] stop \n" +
             "[BACKSPACE] rewind \n" +
             "[ESC] back\n\n",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private val selectionHint = Label(
        "Timeline 1",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 260f,
    )

    private val composer = gameSceneComposerOf("Timeline Scene")
    private val gameScene1 = GameScene1(composer)
    private val gameScene2 = GameScene2(composer)

    private var selection: Timeline = gameScene1.timeline

    init {
        manageContent(
            instructionHint,
            selectionHint,
            composer,
            gameScene1,
            gameScene2
        )

        input[Input.Keys.NUM_1] = {
            selectionHint.text = "Timeline 1"
            selection = gameScene1.timeline
        }
        input[Input.Keys.NUM_2] = {
            selectionHint.text = "Timeline 2"
            selection = gameScene2.timeline
        }
        input[Input.Keys.SPACE] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(selection)
                } else if (composer.isRunning && composer.isPaused) {
                    composer.resume(selection)
                } else if (composer.isRunning && !composer.isPaused) {
                    composer.pause(selection)
                }
            }
        }
        input[Input.Keys.S] = {
            composer.stop(selection)
        }
        input[Input.Keys.BACKSPACE] = {
            composer.rewind(selection)
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

