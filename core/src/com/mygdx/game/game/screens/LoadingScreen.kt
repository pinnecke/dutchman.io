package com.mygdx.game.game.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Screen
import com.mygdx.game.engine.StepAction

class LoadingScreen: Screen() {

    override fun update(dt: Float) {

    }

    override fun loadContents() = object: StepAction {
        override var hasFinished = false

        override fun start() {
            hasFinished = true
        }

        override fun step() {
        }


    }

    override fun unloadContents() = object: StepAction {
        override var hasFinished = false

        override fun start() {
        }

        override fun step() {

        }

    }

    override fun renderWorld(batch: SpriteBatch) {
    }

    override fun renderHud(batch: SpriteBatch) {

    }


}