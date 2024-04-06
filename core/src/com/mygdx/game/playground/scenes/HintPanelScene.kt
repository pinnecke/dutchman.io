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

class HintPanelScene(
    sceneManager: SceneManager
): Scene(
    "Hint Panel Scene",
    sceneManager,
    clearColor = Color.BROWN,
    defaultPanelDimension = 2000f,
    defaultPanelType = PanelDimension.WIDTH
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Hint Panel\n" +
        "[SPACE] show\n" +
        "\n\n" +
        "[ESC] go main menu",
        Color.WHITE,
        200f, 300f,
        borderColor = Engine.colors.darkDimmedGray,
        borderWidth = 3f
    )

    private val background = Decal(
        name = "hint-panel-background",
        sheets = super.sheets,
        position = Position(
            left = 0f,
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
            if (controller.hintPanel.isNotShown) {
                controller.hintPanel.show("WANTED: Master of Mayhem and Mischief!\n" +
                        "\n" +
                        "Reward: A lifetime supply of duct tape and rubber chickens.\n" +
                        "\n" +
                        "Crime: Unleashing uncontrollable outbreaks of laughter in the most serious situations.\n" +
                        "\n" +
                        "Last Seen: Skipping gleefully through the town square, trailed by a mob of tickled citizens.\n" +
                        "\n" +
                        "Approach with caution: May be armed with puns and dad jokes.")
            } else {
                controller.hintPanel.hide()
            }
        }

        background.render(batch)

    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }

}