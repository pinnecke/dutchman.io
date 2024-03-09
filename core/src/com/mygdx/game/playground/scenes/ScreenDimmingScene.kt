package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class ScreenDimmingScene(sceneManager: SceneManager): Scene(
    "Screen Dimming Scene",
    sceneManager,
    clearColor = Color.BROWN,
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Press 0, 1,..., 9 to dim screen to 0%, 10%, ..., 100%\n\n" +
        "M key is dimming speed medium\nS key is dimming speed slow\n" +
        "H key is dimming speed high\n" +
        "ESC key enters main scene",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height / 2f,
    )

    private val overlay = Label(
        "This is an overlay",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height - 200f,
    )

    private var speed = SceneDimmer.DimSpeed.MEDIUM

    init {
        manageContent(
            instructions,
            overlay
        )

        input[Input.Keys.S] = { speed = SceneDimmer.DimSpeed.SLOW }
        input[Input.Keys.M] = { speed = SceneDimmer.DimSpeed.MEDIUM }
        input[Input.Keys.H] = { speed = SceneDimmer.DimSpeed.HIGH }
        input[Input.Keys.NUM_0] = { controller.dimScene(0.0f, speed) }
        input[Input.Keys.NUM_1] = { controller.dimScene(0.1f, speed) }
        input[Input.Keys.NUM_2] = { controller.dimScene(0.2f, speed) }
        input[Input.Keys.NUM_3] = { controller.dimScene(0.3f, speed) }
        input[Input.Keys.NUM_4] = { controller.dimScene(0.4f, speed) }
        input[Input.Keys.NUM_5] = { controller.dimScene(0.5f, speed) }
        input[Input.Keys.NUM_6] = { controller.dimScene(0.6f, speed) }
        input[Input.Keys.NUM_7] = { controller.dimScene(0.7f, speed) }
        input[Input.Keys.NUM_8] = { controller.dimScene(0.8f, speed) }
        input[Input.Keys.NUM_9] = { controller.dimScene(1.0f, speed) }
        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        instructions.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        overlay.render(batch)
    }

}