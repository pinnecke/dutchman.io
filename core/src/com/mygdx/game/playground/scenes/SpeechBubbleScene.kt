package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.hotspots.HudButton
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.SpeechBubble
import com.mygdx.game.engine.objects.SpeechBubblePivot
import com.mygdx.game.playground.MainMenuScene

class SpeechBubbleScene(sceneManager: SceneManager): Scene(
    "Speech Bubble Scene",
    sceneManager
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private var speechPivot = SpeechBubblePivot( 500f, 500f, ::sceneToOverlay)

    private var speechBubble0 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.BLUE,
        speechPivot
    )
    private var speechBubble1 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.DARK_ORANGE,
        speechPivot
    )
    private var speechBubble2 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.GREEN,
        speechPivot
    )
    private var speechBubble3 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.LIGHT_GREEN,
        speechPivot
    )
    private var speechBubble4 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.LIGHT_ORANGE,
        speechPivot
    )
    private var speechBubble5 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.YELLOW,
        speechPivot
    )
    private var speechBubble6 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.PETROL,
        speechPivot
    )
    private var speechBubble7 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.PURPLE,
        speechPivot
    )
    private var speechBubble8 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.RED,
        speechPivot
    )
    private var speechBubble9 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.WHITE,
        speechPivot
    )
    private var speechBubble10 = SpeechBubble(
        "Speech Bubble Batman",
        textColor = TextColor.YELLOW,
        speechPivot
    )


    private val instructions = Label(
        "Text Color\n" +
            "[0] blue\n" +
            "[1] dark orange\n" +
            "[2] green\n" +
            "[3] light green\n" +
            "[4] petrol\n" +
            "[5] purple\n" +
            "[6] red\n" +
            "[7] white\n" +
            "[8] red\n" +
            "[9] white\n" +
            "[V] yellow\n" +
            "\n\n" +
            "[ESC] go main menu",
        Color.WHITE,
        1200f, 1100f,
        borderColor = Engine.colors.darkDimmedGray,
        borderWidth = 3f
    )

    private var hotspot = HudButton(
        160f,
        75f,
        closedPolygon(
            0f, 0f,
            0f, 80f,
            1600f, 1050f,
            50f, 0f
        ),
        this,
        EventFilter.builder()
            .touchDown { _, _ ->
                println("Clicked")
                controller.cinematicModeOn()
                val lines = listOf(
                    Pair(
                        "Oh yes sir.\n" +
                        "I feel so lucky that you happened " +
                        "to capture my ship, then murdered me " +
                        "and everyone on board.\n" +
                        "Yes sir, lucky.",
                        6.27f
                    ),
                    Pair(
                        "I’ve amputated my left foot and have " +
                        "bandaged it with my pants. Strange. All " +
                        "through the operation I was drooling. " +
                        "Drooooling. Just like when I saw the gull. " +
                        "Drooling helplessly. But I made myself wait " +
                        "until after dark. I just counted backward " +
                        "from one hundred … twenty or thirty times!\n" +
                        "Ha! Ha!\n" +
                        "“Then… “I kept telling myself: Cold roast " +
                        "beef. Cold roast beef. Cold roast beef." +
                        "\nCold roast beef.",
                        18.61f
                    ),
                    Pair(
                        "But if a woodchuck could chuck and would chuck some amount of wood, what amount of wood would a woodchuck chuck?",
                        5.9f
                    ),
                    Pair(
                        "Even the bravest of men must dread the horror of this place! Steel your courage boy, now, before you gaze upon the terrible, horrible face of... Skull Island!",
                        11.43f
                    )
                )
                val ran = (lines.indices).shuffled()
                val line = lines[ran[0]]
                speechBubble10.say(
                    text = line.first,
                    duration = line.second,
                    onDone = {
                        println("Speech done")
                        controller.cinematicModeOff()
                    }
                )
            }
                .any()
                .build()
            .build()
    )

    private var batman = Decal(
        name = "batman",
        sheets = super.sheets
    )

    /*private var enterCinematic = runDelayed(
        delay = 2.seconds()) {
        enterCinematicMode {
            println("Cinematic Mode entered!")
            exitCinematic.start()
        }
    }

    private var exitCinematic = runTriggered(
        delay = 5.seconds()) {
        exitCinematicMode {
            println("Cinematic Mode exited!")
        }
    }*/

    init {
        manageContent(
            speechPivot,
            speechBubble0,
            speechBubble1,
            speechBubble2,
            speechBubble3,
            speechBubble4,
            speechBubble5,
            speechBubble6,
            speechBubble7,
            speechBubble8,
            speechBubble9,
            speechBubble10,
            hotspot,
            batman,
            instructions
        )
    }

    override fun update(dt: Float) {
        hotspot.update(dt)
        //enterCinematic.update(dt)
        //exitCinematic.update(dt)
        batman.update(dt)
        speechBubble0.update(dt)
        speechBubble1.update(dt)
        speechBubble2.update(dt)
        speechBubble3.update(dt)
        speechBubble4.update(dt)
        speechBubble5.update(dt)
        speechBubble6.update(dt)
        speechBubble7.update(dt)
        speechBubble8.update(dt)
        speechBubble9.update(dt)
        speechBubble10.update(dt)

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            speechBubble0.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            speechBubble1.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            speechBubble2.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            speechBubble3.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            speechBubble4.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            speechBubble5.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            speechBubble6.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            speechBubble7.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
            speechBubble8.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
            speechBubble9.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            speechBubble10.say("You know, it has been broken.", duration = 1f)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            speechBubble10.abort()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sequence.switch(MainMenuScene::class)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (controller.isInCinematicMode()) {
                controller.cinematicModeOff()
            } else {
                controller.cinematicModeOn()
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.T)) {
            speechPivot.y += 10
            println(sceneToOverlay(speechPivot.x, speechPivot.y))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            speechPivot.y -= 10
            println(sceneToOverlay(speechPivot.x, speechPivot.y))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            speechPivot.x -= 10
            println(sceneToOverlay(speechPivot.x, speechPivot.y))
        }
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            speechPivot.x += 10
            println(sceneToOverlay(speechPivot.x, speechPivot.y))
        }
    }

    override fun render(batch: SpriteBatch) {
        batman.render(batch)
        hotspot.render(LayerType.WORLD, batch)
        instructions.render(batch)
        speechPivot.render(batch)

    }

    override fun overlay(batch: SpriteBatch) {
        hotspot.render(LayerType.HUD, batch)
        speechBubble0.render(batch)
        speechBubble1.render(batch)
        speechBubble2.render(batch)
        speechBubble3.render(batch)
        speechBubble4.render(batch)
        speechBubble5.render(batch)
        speechBubble6.render(batch)
        speechBubble7.render(batch)
        speechBubble8.render(batch)
        speechBubble9.render(batch)
        speechBubble10.render(batch)
    }


}