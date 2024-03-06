package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.hotspots.HudButton
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.SpeechBubble
import com.mygdx.game.engine.objects.SpeechBubblePivot

class SpeechBubbleScene: Scene() {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private var speechPivot = SpeechBubblePivot(500f, 500f, ::sceneToOverlay)

    private var speechBubble = SpeechBubble(
        textColor = Color.YELLOW,
        speechPivot
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
                speechBubble.say(
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

    private var batman = FrameAnimation(
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

    override fun update(dt: Float) {
        hotspot.update(dt)
        //enterCinematic.update(dt)
        //exitCinematic.update(dt)
        batman.update(dt)
        speechBubble.update(dt)

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            speechBubble.say("This looks slightly weird.", duration = 2f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            speechBubble.say("Righty, right.", duration = 2f)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            speechBubble.abort()
        }
        if (Gdx.input.isKeyPressed(Input.Keys.C)) {
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

    override fun create() {
        hotspot.loadContent()
        batman.create()
        speechPivot.create()
        speechBubble.create()
    }

    override fun destroy() {
        hotspot.unloadContent()
        batman.destroy()
        speechBubble.destroy()
        speechPivot.destroy()
    }

    override fun render(batch: SpriteBatch) {
        batman.render(batch)
        hotspot.render(LayerType.WORLD, batch)
        speechPivot.render(batch)

    }

    override fun overlay(batch: SpriteBatch) {
        hotspot.render(LayerType.HUD, batch)
        speechBubble.render(batch)
    }


}