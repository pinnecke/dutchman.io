package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.*
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class NarratorSpeechBubbleScene(
    sceneManager: SceneManager
): Scene(
    "Narrator Speech Bubble Scene",
    sceneManager,
    clearColor = Color.FIREBRICK,
    defaultPanelDimension = 2000f,
    defaultPanelType = PanelDimension.WIDTH
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Narrator Speech Bubble\n" +
        "[1] start north west\n" +
        "[2] start north east\n" +
        "[3] start south west\n" +
        "[4] start south east\n" +
        "\n\n" +
        "[ESC] go main menu",
        Color.WHITE,
        700f, 700f,
        borderColor = Engine.colors.darkDimmedGray,
        borderWidth = 3f
    )

    private val narratorNw = NarratorSpeechBubble(
        contentIdentifier = "Guy talking in offscreen",
        textColor = Color.WHITE,
        location = NarratorSpeechBubbleLocation.NORTH_WEST
    )

    private val narratorNe = NarratorSpeechBubble(
        contentIdentifier = "Guy talking in offscreen",
        textColor = Color.WHITE,
        location = NarratorSpeechBubbleLocation.NORTH_EAST
    )

    private val narratorSw = NarratorSpeechBubble(
        contentIdentifier = "Guy talking in offscreen",
        textColor = Color.WHITE,
        location = NarratorSpeechBubbleLocation.SOUTH_WEST
    )
    private val narratorSe = NarratorSpeechBubble(
        contentIdentifier = "Guy talking in offscreen",
        textColor = Color.WHITE,
        location = NarratorSpeechBubbleLocation.SOUTH_EAST
    )


    init {
        manageContent(
            instructions,
            narratorNw,
            narratorNe,
            narratorSw,
            narratorSe
        )

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        narratorNw.update(dt)
        narratorNe.update(dt)
        narratorSw.update(dt)
        narratorSe.update(dt)

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            narratorNw.say(
                "The face is finished at last - Wonderful black and white, as all things should be.\n" +
                     "I am glad I decided to keep the dress these past two years.\n" +
                     "The face is perfect, a thing of true beauty... \n" +
                     "A face that can shelter me from the world and hide my weary senses. \n" +
                     "A face which I can finally stare down in the mirror",
                duration = 20f
            )
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            narratorNe.say(
                "From this point on, I've decided to write down everything I see and experience which might possibly have a bearing upon my nocturnal mission.\nThis journal will be a complete record of my deeds which I can refer back to and a voucher to show the angels when they come looking for me on Judgement Day.",
                duration = 25f
            )
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            narratorSw.say(
                "I'll start tonight with the woman and her killers.", duration = 5f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            narratorSe.say(
                "Counted seventeen transients in neighborhood...\nthis morning.\nMust remember to begin looking for new apartment...\ntomorrow.", duration = 10f)
        }
    }

    override fun render(batch: SpriteBatch) {
        instructions.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        narratorNw.render(batch)
        narratorNe.render(batch)
        narratorSw.render(batch)
        narratorSe.render(batch)
    }

}