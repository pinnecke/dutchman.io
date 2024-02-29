package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Screen
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.objects.centered
import com.mygdx.game.engine.stdx.once
import com.mygdx.game.engine.stdx.runDelayed
import com.mygdx.game.engine.stdx.seconds
import com.mygdx.game.engine.stdx.value

class SplashScreen: Screen(
    clearColor = Color.WHITE
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
        enterScene(GameMenuScreen::class)
    }

    override fun update(dt: Float) {
        startLogoAnimation.update(dt)
        exitScreen.update(dt)
        nemonicLogo.update(dt)

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            enterScene(GameMenuScreen::class)
        }
    }

    override fun loadContents() {
        with(nemonicLogo) {
            create()
            position = centered(
                screen = thisScreen(),
                surface = { surface },
                offset = value(Position({ surface.width / 4f }, { surface.height / 2f + 50f }))
            )
        }
    }

    override fun unloadContents() {
        nemonicLogo.destroy()
    }

    override fun render(batch: SpriteBatch) {
        nemonicLogo.render(batch)
    }

    override fun renderOverlay(batch: SpriteBatch) {

    }


}
