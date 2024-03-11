package com.mygdx.game.playground

import com.mygdx.game.engine.DutchmanGame
import com.mygdx.game.engine.SceneManager
import com.mygdx.game.playground.scenes.*
import com.mygdx.game.playground.scenes.camera.fixed.CameraMovementStaticShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementCrashZoomShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementPanShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementWhipPanShotScene
import com.mygdx.game.playground.scenes.camera.pan.CameraMovementZoomShotScene
import com.mygdx.game.playground.scenes.timeline.TimelineScene

class PlaygroundGame(sceneManager: SceneManager): DutchmanGame(sceneManager) {

    companion object {
        const val namespace = "playground"
        val splash = MainMenuScene::class
    }

    override val scenes = listOf(
        SplashScreenScene(sceneManager),
        MainMenuScene(sceneManager),
        SpeechBubbleScene(sceneManager),
        ScreenDimmingScene(sceneManager),
        CinematicModeScene(sceneManager),
        StaticShotScene(sceneManager),
        PanShotScene(sceneManager),
        TimelineScene(sceneManager),
        CameraMovementStaticShotScene(sceneManager),
        CameraMovementPanShotScene(sceneManager),
        CameraMovementWhipPanShotScene(sceneManager),
        CameraMovementZoomShotScene(sceneManager),
        CameraMovementCrashZoomShotScene(sceneManager)
    )

}