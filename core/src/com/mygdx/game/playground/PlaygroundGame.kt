package com.mygdx.game.playground

import com.mygdx.game.engine.DutchmanGame
import com.mygdx.game.playground.scenes.*

class PlaygroundGame: DutchmanGame {
    override val namespace = "playground"
    override val splash = MainMenuScene::class
    override val scenes = listOf(
        SplashScreenScene(),
        MainMenuScene(),
        SpeechBubbleScene(),
        ScreenDimmingScene(),
        CinematicModeScene(),
        WindowSizingScene(),
        CameraModeScene()
    )
}