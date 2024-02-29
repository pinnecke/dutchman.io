package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.mygdx.game.engine.DutchmanEngine
import com.mygdx.game.engine.ScreenManager
import com.mygdx.game.playground.Playground
import com.mygdx.game.playground.screens.GameMenuScreen
import com.mygdx.game.playground.screens.SplashScreen

class MyGdxGame : ApplicationAdapter() {

    private var dutchman = DutchmanEngine()

    override fun create() = dutchman.create(
        Playground()
    )
    override fun render() = dutchman.render()
    override fun resize(width: Int, height: Int) = dutchman.resize(width, height)
    override fun dispose() = dutchman.dispose()
}