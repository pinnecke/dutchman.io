package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.mygdx.game.engine.ScreenManager
import com.mygdx.game.game.screens.GameMenuScreen
import com.mygdx.game.game.screens.LoadingScreen
import com.mygdx.game.game.screens.SplashScreen

class MyGdxGame : ApplicationAdapter() {

    private var screenManager = ScreenManager()

    override fun create() {
        screenManager.register(
            SplashScreen(),
            GameMenuScreen()
        )
        screenManager.startup(SplashScreen::class)
    }

    override fun render() {
        screenManager.update(Gdx.graphics.deltaTime)
        screenManager.render()
    }

    override fun resize(width: Int, height: Int) {
        screenManager.resize(width, height)
    }

    override fun dispose() {
        screenManager.shutdown()
    }
}