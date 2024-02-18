package com.mygdx.game.game.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Screen
import com.mygdx.game.engine.StepAction

class LoadingScreen: Screen() {

    private var img: Texture? = null

    override fun update(dt: Float) {

    }

    override fun loadContents() = object: StepAction {
        override var hasFinished = false

        override fun start() {
            img = Texture("loading.png")
            hasFinished = true
        }

        override fun step() {
        }


    }

    override fun unloadContents() = object: StepAction {
        override var hasFinished = false

        override fun start() {
            img!!.dispose()
        }

        override fun step() {

        }

    }

    override fun renderWorld(batch: SpriteBatch) {
        batch!!.draw(img, super.width / 2f - 5f, super.height / 2f + 15f, 100f, 100f)
    }

    override fun renderHud(batch: SpriteBatch) {

    }


}