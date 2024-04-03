package com.mygdx.game.playground

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.scenes.*
import com.mygdx.game.playground.scenes.camera.fixed.CameraMovementStaticShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementCrashZoomShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementPanShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementWhipPanShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementZoomShotScene
import com.mygdx.game.playground.scenes.decal.DecalScene
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
        "[5]   \t camera static shot \n" +
        "[6]   \t camera pan shot\n" +
        "[7]   \t camera whip pan shot\n" +
        "[8]   \t camera zoom shot\n" +
        "[9]   \t camera crash zoom shot\n" +
        "[Y]   \t decal effects\n" +
        "[X]   \t scene post effects\n" +
        "[C]   \t time line demo\n" +
        "[V]   \t scene camera shaking effect\n" +
        "[B]   \t hint panel\n" +
        "[N]   \t narrator speech bubble\n",
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
        input[Input.Keys.NUM_6] = { sequence.switch(CameraMovementPanShotScene::class) }
        input[Input.Keys.NUM_7] = { sequence.switch(CameraMovementWhipPanShotScene::class) }
        input[Input.Keys.NUM_8] = { sequence.switch(CameraMovementZoomShotScene::class) }
        input[Input.Keys.NUM_9] = { sequence.switch(CameraMovementCrashZoomShotScene::class) }
        input[Input.Keys.Y] = { sequence.switch(DecalScene::class) }
        input[Input.Keys.X] = { sequence.switch(ScenePostEffectsScene::class) }
        input[Input.Keys.C] = { sequence.switch(TimelineScene::class) }
        input[Input.Keys.V] = { sequence.switch(ScreenShakingScene::class) }
        input[Input.Keys.B] = { sequence.switch(HintPanelScene::class) }
        input[Input.Keys.N] = { sequence.switch(NarratorSpeechBubbleScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        instructions.render(batch)
    }

}