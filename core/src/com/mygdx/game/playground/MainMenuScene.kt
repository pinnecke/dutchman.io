package com.mygdx.game.playground

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Engine
import com.mygdx.game.engine.Scene
import com.mygdx.game.engine.SceneController
import com.mygdx.game.engine.SequenceController
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.scenes.*

class MainMenuScene: Scene(
    clearColor = Color.PURPLE
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "[1]   \t cinematic mode\n" +
        "[2]   \t screen dimming\n" +
        "[3]   \t speech bubble\n" +
        "[4]   \t splash screen\n" +
        "[5]   \t window sizing\n" +
        "[6]   \t static shot demo\n",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height,
    )

    override fun create() {
        instructions.create()

        input[Input.Keys.NUM_1] = { sequence.switch(CinematicModeScene::class) }
        input[Input.Keys.NUM_2] = { sequence.switch(ScreenDimmingScene::class) }
        input[Input.Keys.NUM_3] = { sequence.switch(SpeechBubbleScene::class) }
        input[Input.Keys.NUM_4] = { sequence.switch(SplashScreenScene::class) }
        input[Input.Keys.NUM_5] = { sequence.switch(WindowSizingScene::class) }
        input[Input.Keys.NUM_6] = { sequence.switch(StaticShotScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        instructions.render(batch)
    }

    override fun destroy(){
        instructions.destroy()
    }

}