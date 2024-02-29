package com.mygdx.game.playground

import com.mygdx.game.engine.DutchmanGame
import com.mygdx.game.playground.scenes.DimScreen
import com.mygdx.game.playground.scenes.GameMenuScreen
import com.mygdx.game.playground.scenes.SplashScreen

class Playground: DutchmanGame {
    override val namespace = "playground"
    override val splashScreen = DimScreen::class
    override val screens = listOf(
        SplashScreen(),
        GameMenuScreen(),
        DimScreen()
    )
}