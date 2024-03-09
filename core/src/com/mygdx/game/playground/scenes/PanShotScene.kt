package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.stdx.value
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class StaticShotScene: Scene(
    clearColor = Color.GRAY
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
        left = -1000f,
        bottom = -2500f,
        dimension = 8000f,
        type = ShotDimension.WIDTH
    )

    private var dialogAFrame = FrameAnimation(
        name = "static-shot-dialog-a",
        sheets = super.sheets
    )

    private var dialogBFrame = FrameAnimation(
        name = "static-shot-dialog-b",
        sheets = super.sheets,
        position = value(Position(
            left = { 2852f + 100f },
            bottom = { 0f }
        ))
    )

    private val dialogAShot = StaticShot(
        caption = "Dialog A",
        factory = shotFactory,
        left = 500f,
        bottom = 0f,
        dimension = 1150f,
        type = ShotDimension.HEIGHT
    )

    private val dialogBShot = StaticShot(
        caption = "Dialog B",
        factory = shotFactory,
        left = 2852f + 100f + 500f,
        bottom = 0f,
        dimension = 1140f,
        type = ShotDimension.HEIGHT
    )

    private var compositionFrame = FrameAnimation(
        name = "static-shot-composition",
        sheets = super.sheets,
        position = value(Position(
            left = { 0f },
            bottom = { -1600f }
        ))
    )

    private val compositionShot = StaticShot(
        caption = "Composition",
        factory = shotFactory,
        left = 0f,
        bottom = -1600f,
        dimension = 1936f,
        type = ShotDimension.WIDTH
    )

    init {
        initialShot = overviewShot
    }

    override fun create() {
        instructions.create()
        instructions.visible = true

        dialogAFrame.create()
        dialogBFrame.create()
        compositionFrame.create()

        dialogAShot.create()
        dialogBShot.create()
        overviewShot.create()
        compositionShot.create()

        input[Input.Keys.NUM_1] = {
            instructions.visible = false
            dialogAShot.cut()
        }
        input[Input.Keys.NUM_2] = {
            instructions.visible = false
            dialogBShot.cut()
        }
        input[Input.Keys.NUM_3] = {
            instructions.visible = false
            compositionShot.cut()
        }
        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
    }

    override fun render(batch: SpriteBatch) {
        dialogAFrame.render(batch)
        dialogBFrame.render(batch)
        compositionFrame.render(batch)

        dialogAShot.render(batch)
        dialogBShot.render(batch)
        overviewShot.render(batch)
        compositionShot.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }

    override fun destroy(){
        instructions.destroy()

        dialogAFrame.destroy()
        dialogBFrame.destroy()
        compositionFrame.destroy()

        overviewShot.destroy()
        dialogAShot.destroy()
        dialogBShot.destroy()
        compositionShot.destroy()
    }

}