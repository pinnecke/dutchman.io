package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Engine
import com.mygdx.game.engine.Scene
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Rectangle
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class CinematicModeScene: Scene() {
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Press 1 to enter cinematic mode, and 2 to leave cinematic mode\nESC key enters main scene",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height / 2f,
    )

    private val rectangle = Rectangle(
        x = 0f, y = 0f,
        width = Engine.canvas.surface.width, height = Engine.canvas.surface.height,
        color = Color.ORANGE
    )

    override fun create() {
        instructions.create()
        rectangle.create()

        input[Input.Keys.NUM_1] = { cinematicModeOn() }
        input[Input.Keys.NUM_2] = { cinematicModeOff() }
        input[Input.Keys.ESCAPE] = { enterScene(MainMenuScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        rectangle.render(batch)
        instructions.render(batch)
    }

    override fun destroy(){
        instructions.destroy()
        rectangle.destroy()
    }

}