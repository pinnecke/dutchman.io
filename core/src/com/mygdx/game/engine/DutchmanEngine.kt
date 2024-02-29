package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.mygdx.game.playground.screens.SplashScreen
import kotlin.reflect.KClass

interface DutchmanGame {
    val namespace: String
    val splashScreen: KClass<*>
    val screens: Array<Screen>
}

class DutchmanEngine {
    private var screenManager: ScreenManager? = null

    fun create(game: DutchmanGame) {
        screenManager = ScreenManager(game.namespace)
        with (screenManager!!) {
            register(game.screens)
            startup(game.splashScreen)
        }
    }

    fun render() = with(screenManager!!) {
        update(Gdx.graphics.deltaTime)
        render()
    }

    fun resize(width: Int, height: Int) =
        screenManager!!.resize(width, height)

    fun dispose() =
        screenManager!!.shutdown()

}