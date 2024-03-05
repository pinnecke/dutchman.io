package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Scene
import com.mygdx.game.engine.SceneController
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.objects.centered
import com.mygdx.game.engine.stdx.once
import com.mygdx.game.engine.stdx.runDelayed
import com.mygdx.game.engine.stdx.seconds
import com.mygdx.game.engine.stdx.value
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class SplashScreenScene: Scene(
    clearColor = Color.WHITE
) {

    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

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

    private var exitScene = runDelayed(
        delay = 5.seconds()) {
        controller.enterScene(MainMenuScene::class)
    }

    override fun create() {
        with(nemonicLogo) {
            create()
            position = centered(
                scene = self(),
                surface = { surface },
                offset = value(Position({ surface.width / 4f }, { surface.height / 2f + 50f }))
            )
        }

        input[Input.Keys.SPACE] = { controller.enterScene(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        startLogoAnimation.update(dt)
        exitScene.update(dt)
        nemonicLogo.update(dt)
    }

    override fun destroy() {
        nemonicLogo.destroy()
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        nemonicLogo.render(batch)
    }
}
