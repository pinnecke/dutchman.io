package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.Scene
import com.mygdx.game.engine.SceneController
import com.mygdx.game.engine.SceneManager
import com.mygdx.game.engine.SequenceController
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.stdx.once
import com.mygdx.game.engine.stdx.runDelayed
import com.mygdx.game.engine.stdx.seconds
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class SplashScreenScene(sceneManager: SceneManager): Scene(
    "Splash Screen Scene",
    sceneManager,
    clearColor = Color.WHITE,
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private var nemonicLogo = Decal(
        name = "nemonic-logo",
        iterations = once(),
        sheets = super.sheets
    )

    private var startLogoAnimation = runDelayed(
        delay = 1.seconds()) {
        nemonicLogo.animiate = true
    }

    private var exitScene = runDelayed(
        delay = 5.seconds()) {
        sequence.switch(MainMenuScene::class)
    }

    init {
        manageContent(
            nemonicLogo
        )

       /* with(nemonicLogo) {
            position = centered(
                scene = self(),
                surface = { surface },
                offset = value(Position({ surface.width / 4f }, { surface.height / 2f + 50f }))
            )
        }*/

        input[Input.Keys.SPACE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        startLogoAnimation.update(dt)
        exitScene.update(dt)
        nemonicLogo.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        nemonicLogo.render(batch)
    }
}
