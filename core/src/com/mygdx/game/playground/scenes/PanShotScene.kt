package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.shots.PanTiltShot
import com.mygdx.game.engine.shots.StaticShot
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class PanShotScene(sceneManager: SceneManager): Scene(
    "Pan Shot Scene",
    sceneManager,
    clearColor = Color.GRAY,
) {
    private val sequence = SequenceController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "[1] dialog shot A\n" +
             "[2] dialog shot B\n" +
             "[3] composition shot B\n" +
             "[ESC] back\n\n" +
             "Static shots used for dialogs, compositions, single actor performance, trap a character",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private val overviewShot = StaticShot(
        caption = "Developer View",
        factory = shotFactory,
        left = -2000f,
        bottom = -1500f,
        dimension = 8000f,
        type = ShotDimension.WIDTH,
        duration = Float.POSITIVE_INFINITY
    )

    private var widescreenFrame = FrameAnimation(
        name = "pan-shot-widescreen",
        sheets = super.sheets
    )

    private val widescreenShot = PanTiltShot(
        caption = "Example",
        factory = shotFactory,
        beginLeft = 200f,
        beginBottom = 200f,
        beginDimension = 1150f,
        beginType = ShotDimension.HEIGHT,
        endLeft = 1620f,
        endBottom = 200f,
        endDimension = 1150f,
        endType = ShotDimension.HEIGHT,
        duration = 2f,
        onUpdates = { _,_,_ -> println("Done") }
    )

    init {
        manageContent(
            instructions,
            overviewShot,
            widescreenFrame,
            widescreenShot
        )

        initialShot = overviewShot

        instructions.visible = true

        input[Input.Keys.NUM_1] = {
            instructions.visible = false
            widescreenShot.debuggable = false
            widescreenShot.cut()
        }
        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        widescreenShot.update(dt)
        overviewShot.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        widescreenFrame.render(batch)

        widescreenShot.render(batch)
        overviewShot.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }

}