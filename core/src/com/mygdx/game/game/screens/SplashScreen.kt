package com.mygdx.game.game.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Screen
import com.mygdx.game.engine.StepAction

class SplashScreen: Screen() {

    private var img: Texture? = null
    private var elapsed: Float = 0f
    private var duration: Float = 3f

    override fun update(dt: Float) {
        elapsed += dt
        if (elapsed >= duration) {
            elapsed = 0f
            switch(GameMenuScreen::class)
        }
    }

    override fun loadContents() = object: StepAction {
        override var hasFinished: Boolean = false

        override fun start() {
            img = Texture("badlogic.jpg")

            elapsed = 0f
            hasFinished = true
        }

        override fun step() {

        }

    }

    override fun unloadContents() = object: StepAction {
        override var hasFinished = false

        override fun start() {
            img!!.dispose()
            hasFinished = true
        }

        override fun step() {

        }

    }

    override fun renderWorld(batch: SpriteBatch) {
        batch!!.draw(img, super.width / 2f - 5f, super.height / 2f - 5f)
    }

    override fun renderHud(batch: SpriteBatch) {

    }


}