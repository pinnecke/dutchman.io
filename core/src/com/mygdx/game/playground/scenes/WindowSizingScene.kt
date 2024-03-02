package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Engine
import com.mygdx.game.engine.Scene
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class WindowSizingScene: Scene() {
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Press 1 to enter 16:9, and 2 to enter 4:3\nESC key enters main scene",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height / 2f,
    )

    override fun create() {
        instructions.create()

        input[Input.Keys.NUM_1] = { Gdx.graphics.setWindowedMode(1920/2, 1080/2) }
        input[Input.Keys.NUM_2] = { Gdx.graphics.setWindowedMode(1440/2, 1080/2) }
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