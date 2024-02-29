package com.mygdx.game.playground

import com.mygdx.game.engine.DutchmanGame
import com.mygdx.game.engine.Screen
import com.mygdx.game.playground.screens.GameMenuScreen
import com.mygdx.game.playground.screens.SplashScreen
import kotlin.reflect.KClass

class Playground: DutchmanGame {
    override val namespace: String = "playground"
    override val splashScreen: KClass<*> = SplashScreen::class
    override val screens: Array<Screen> = arrayOf(
        GameMenuScreen(),
        SplashScreen()
    )
}