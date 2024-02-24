package com.mygdx.game.game.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Screen
import com.mygdx.game.engine.StepAction
import com.mygdx.game.engine.sprites.FrameAnimation
import com.mygdx.game.engine.sprites.Position
import com.mygdx.game.engine.sprites.centered
import com.mygdx.game.engine.stdx.once
import com.mygdx.game.engine.stdx.runDelayed
import com.mygdx.game.engine.stdx.seconds
import com.mygdx.game.engine.stdx.value

class SplashScreen: Screen(
    backgroundColor = Color.WHITE
) {

    private var nemonicLogo = FrameAnimation(
        name = "nemonic-logo",
        scale = 0.5f,
        iterations = once(),
        sheets = super.sheets
    )

    private var startLogoAnimation = runDelayed(
        delay = 1.seconds()) {
        nemonicLogo.start()
    }

    private var exitScreen = runDelayed(
        delay = 5.seconds()) {
        switch(GameMenuScreen::class)
    }

    override fun update(dt: Float) {
        startLogoAnimation.update(dt)
        exitScreen.update(dt)
        nemonicLogo.update(dt)
    }

    override fun loadContents() = object: StepAction {
        override var hasFinished: Boolean = false

        override fun start() {
            with(nemonicLogo) {
                load()
                position = centered(
                    screen = thisScreen(),
                    surface = { surface },
                    offset = value(Position({ surface.width / 4f }, { surface.height / 2f + 50f }))
                )
            }

            hasFinished = true
        }

        override fun step() {

        }


    }


    override fun unloadContents() = object: StepAction {
        override var hasFinished = false

        override fun start() {
            nemonicLogo.unload()
            hasFinished = true
        }

        override fun step() {

        }

    }

    override fun renderWorld(batch: SpriteBatch) {
        nemonicLogo.render(batch)
    }

    override fun renderHud(batch: SpriteBatch) {

    }


}
