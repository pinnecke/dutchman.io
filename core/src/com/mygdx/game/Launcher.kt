package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.mygdx.game.engine.DutchmanEngine
import com.mygdx.game.playground.PlaygroundGame

class Launcher : ApplicationAdapter() {

    private var dutchman = DutchmanEngine()

    override fun create() = dutchman.create(
        PlaygroundGame()
    )
    override fun render() = dutchman.render()
    override fun resize(width: Int, height: Int) = dutchman.resize(width, height)
    override fun dispose() = dutchman.dispose()
}