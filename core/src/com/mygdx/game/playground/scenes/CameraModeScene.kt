package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Rectangle
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class CameraModeScene: Scene(
    clearColor = Color.GRAY
) {
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Press 1 to enter cinematic mode, and 2 to leave cinematic mode\nESC key enters main scene",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height / 2f,
    )

    override fun create() {
        instructions.create()

        input[Input.Keys.NUM_1] = { cinematicModeOn() }
        input[Input.Keys.NUM_2] = { cinematicModeOff() }
        input[Input.Keys.ESCAPE] = { enterScene(MainMenuScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        instructions.render(batch)
    }

    override fun destroy(){
        instructions.destroy()
    }

}