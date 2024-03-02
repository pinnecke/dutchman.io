package com.mygdx.game.playground

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Engine
import com.mygdx.game.engine.Scene
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.scenes.*

class MainMenuScene: Scene(
    clearColor = Color.PURPLE
) {
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "[1]   \t cinematic mode\n" +
        "[2]   \t screen dimming\n" +
        "[3]   \t speech bubble\n" +
        "[4]   \t splash screen\n" +
        "[5]   \t window sizing\n" +
        "[6]   \t camera modes\n",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height,
    )

    override fun create() {
        instructions.create()

        input[Input.Keys.NUM_1] = { enterScene(CinematicModeScene::class) }
        input[Input.Keys.NUM_2] = { enterScene(ScreenDimmingScene::class) }
        input[Input.Keys.NUM_3] = { enterScene(SpeechBubbleScene::class) }
        input[Input.Keys.NUM_4] = { enterScene(SplashScreenScene::class) }
        input[Input.Keys.NUM_5] = { enterScene(WindowSizingScene::class) }
        input[Input.Keys.NUM_6] = { enterScene(CameraModeScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        instructions.render(batch)
    }

    override fun destroy(){
        instructions.destroy()
    }

}