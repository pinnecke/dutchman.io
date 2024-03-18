package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class ScenePostEffectsScene(sceneManager: SceneManager): Scene(
    "Scene Post Effects",
    sceneManager,
    clearColor = Color.BROWN,
) {
    private val sequence = SequenceController(this)
    private val postEffects = PostEffects(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val instructions = Label(
        "effects\n" +
        "[1] blur\n" +
        "[2] gamma\n" +
        "[3] crt\n" +
        "[4] grain\n" +
        "[5] white\n" +
        "[6] vignette\n" +
        "\n\n" +
        "controls\n" +
        "[0] increase\n" +
        "[9] decrease\n" +
        "\n\n" +
        "[ESC] back",
        Color.WHITE,
        1400f, Engine.canvas.safeZone.height - 200f,
    )

    private val enabledEffects = Label(
        "",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height - 200f,
    )

    private val decal = Decal(
        name = "batman",
        sheets = super.sheets
    )

    private var amount = 0.5f

    init {
        manageContent(
            instructions,
            enabledEffects,
            decal
        )

        input[Input.Keys.NUM_9] = { amount -= 0.1f; println(amount) }
        input[Input.Keys.NUM_0] = { amount += 0.1f; println(amount) }

        input[Input.Keys.NUM_1] = { inputSafe {
            if (!postEffects.blur.enabled || amount != postEffects.blur.amount) {
                postEffects.blur.configure(amount = amount, duration = 1.0f)
            } else {
                postEffects.blur.enabled = false
            }
        }}

        input[Input.Keys.NUM_2] = { inputSafe {
            if (!postEffects.gamma.enabled || amount != postEffects.gamma.amount) {
                postEffects.gamma.configure(amount = amount, duration = 1.0f)
            } else {
                postEffects.gamma.enabled = false
            }
        }}

        input[Input.Keys.NUM_3] = { inputSafe {
            if (!postEffects.crt.enabled || amount != postEffects.crt.amount) {
                postEffects.crt.configure(amount = amount, duration = 1.0f)
            } else {
                postEffects.crt.enabled = false
            }
        }}

        input[Input.Keys.NUM_4] = { inputSafe {
            if (!postEffects.grain.enabled || amount != postEffects.grain.amount) {
                postEffects.grain.configure(amount = amount, duration = 1.0f)
            } else {
                postEffects.grain.enabled = false
            }
        }}

        input[Input.Keys.NUM_5] = { inputSafe {
            if (!postEffects.white.enabled || amount != postEffects.white.amount) {
                postEffects.white.configure(amount = amount, duration = 1.0f)
            } else {
                postEffects.white.enabled = false
            }
        }}

        input[Input.Keys.NUM_6] = { inputSafe {
            if (!postEffects.vignette.enabled || amount != postEffects.vignette.amount) {
                postEffects.vignette.configure(amount = amount, duration = 1.0f)
            } else {
                postEffects.vignette.enabled = false
            }
        }}

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        inputDelayer.update(dt)
        enabledEffects.update(dt)

        enabledEffects.text = "amount: $amount\n\n"
        if (postEffects.blur.enabled) {
            enabledEffects.text += "blur (${postEffects.blur.amount})\n"
        }
        if (postEffects.gamma.enabled) {
            enabledEffects.text += "gamma (${postEffects.gamma.amount})\n"
        }
        if (postEffects.crt.enabled) {
            enabledEffects.text += "crt (${postEffects.crt.amount})\n"
        }
        if (postEffects.grain.enabled) {
            enabledEffects.text += "grain (${postEffects.grain.amount})\n"
        }
        if (postEffects.white.enabled) {
            enabledEffects.text += "white (${postEffects.white.amount})\n"
        }
        if (postEffects.vignette.enabled) {
            enabledEffects.text += "vignette (${postEffects.vignette.amount})\n"
        }
    }

    override fun render(batch: SpriteBatch) {
        decal.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
        enabledEffects.render(batch)
    }

    private fun inputSafe(action: () -> Unit) {
        if (inputDelayer.isNotRunning) {
            inputDelayer.start()
            action()
        }
    }

}