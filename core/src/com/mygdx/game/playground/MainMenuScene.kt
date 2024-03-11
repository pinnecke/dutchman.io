package com.mygdx.game.playground

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.scenes.CinematicModeScene
import com.mygdx.game.playground.scenes.ScreenDimmingScene
import com.mygdx.game.playground.scenes.SpeechBubbleScene
import com.mygdx.game.playground.scenes.SplashScreenScene
import com.mygdx.game.playground.scenes.timeline.CameraMovementStaticShotScene
import com.mygdx.game.playground.scenes.timeline.TimelineScene

class MainMenuScene(sceneManager: SceneManager): Scene(
    "Main Menu Scene",
    sceneManager,
    clearColor = Color.PURPLE,
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "[1]   \t cinematic mode\n" +
        "[2]   \t screen dimming\n" +
        "[3]   \t speech bubble\n" +
        "[4]   \t splash screen\n" +
        "[5]   \t camera movements \n" +
        "[6]   \t \n" +
        "[7]   \t \n" +
        "[8]   \t time line demo\n",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height - 50f,
    )

    init {
        managedContent.add(instructions)

        input[Input.Keys.NUM_1] = { sequence.switch(CinematicModeScene::class) }
        input[Input.Keys.NUM_2] = { sequence.switch(ScreenDimmingScene::class) }
        input[Input.Keys.NUM_3] = { sequence.switch(SpeechBubbleScene::class) }
        input[Input.Keys.NUM_4] = { sequence.switch(SplashScreenScene::class) }
        input[Input.Keys.NUM_5] = { sequence.switch(CameraMovementStaticShotScene::class) }


        input[Input.Keys.NUM_8] = { sequence.switch(TimelineScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        instructions.render(batch)
    }

}