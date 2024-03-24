package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class ScreenShakingScene(sceneManager: SceneManager): Scene(
    "Screen Shaking Scene",
    sceneManager,
    clearColor = Color.BROWN,
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Shake Effect\n" +
        "[SPACE] on/off\n" +
        "\n\n" +
        "Speed\n" +
        "[1] low\n" +
        "[2] medium\n" +
        "[3] high\n" +
        "\n\n" +
        "Amount\n" +
        "[4] low\n" +
        "[5] medium\n" +
        "[6] high\n" +
        "\n\n" +
        "Horizontal\n" +
        "[7] less\n" +
        "[8] more\n" +
        "\n\n" +
        "Vertical\n" +
        "[9] positive\n" +
        "[0] negative\n" +
        "\n\n" +
        "[ESC] go main menu",
        Color.WHITE,
        200f, 1100f,
        borderColor = Engine.colors.darkDimmedGray,
        borderWidth = 3f
    )

    private val background = Decal(
        name = "explosion",
        sheets = super.sheets,
        position = Position(
            left = -100f,
            bottom = -400f
        )
    )

    init {
        manageContent(
            instructions,
            background
        )

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (controller.shake.effect.isNotRunning) {
                controller.shake.effect.vertical = 0f
                controller.shake.effect.horizontal = 1f
                controller.shake.effect.start()
            } else {
                controller.shake.effect.stop()
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            controller.shake.effect.speed = 10f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            controller.shake.effect.speed = 25f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            controller.shake.effect.speed = 40f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            controller.shake.effect.amount = 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            controller.shake.effect.amount = 5f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            controller.shake.effect.amount = 10f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            controller.shake.effect.horizontal = 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            controller.shake.effect.horizontal = 5f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            controller.shake.effect.vertical = 1f
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            controller.shake.effect.vertical = -1f
        }

        background.render(batch)

    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }

}