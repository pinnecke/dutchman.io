package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.shots.StaticShot
import com.mygdx.game.engine.stdx.value
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class StaticShotScene(sceneManager: SceneManager): Scene(
    "Static Shot Scene",
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
        left = -1000f,
        bottom = -2500f,
        dimension = 8000f,
        type = ShotDimension.WIDTH,
        duration = Float.POSITIVE_INFINITY
    )

    private var dialogA = Decal(
        name = "static-shot-dialog-a",
        sheets = super.sheets
    )

    private var southParkLipsDecal = Decal(
        name = "south-park-lips",
        sheets = super.sheets,
        position = Position(
            left = 1150f,
            bottom = 620f
        ),
        flipX = true,
        visible = false
    )

    private var dialogBDecal = Decal(
        name = "static-shot-dialog-b",
        sheets = super.sheets,
        position = Position(
            left = 2852f + 100f,
            bottom = 0f
        )
    )

    private val dialogAShot = StaticShot(
        caption = "Dialog A",
        factory = shotFactory,
        left = 500f,
        bottom = 0f,
        dimension = 1150f,
        type = ShotDimension.HEIGHT,
        duration = 6f,
        onStart = {
            println("Dialog shot A entered")
        },
        onUpdates = { dt, elapsed, progress ->
            println("Dialog shot A update: $dt, $elapsed, $progress")
            if (elapsed > 2f && elapsed <= 4.5f) {
                southParkLipsDecal.visible = true
                southParkLipsDecal.animiate = true
            } else if (elapsed > 4.5f) {
                southParkLipsDecal.visible = false
                southParkLipsDecal.animiate = false
            }
        },
        onDone = {
            dialogBShot.debuggable = false
            dialogBShot.cut()
        }
    )

    private val dialogBShot = StaticShot(
        caption = "Dialog B",
        factory = shotFactory,
        left = 2852f + 100f + 500f,
        bottom = 0f,
        dimension = 1140f,
        type = ShotDimension.HEIGHT,
        duration = 3f
    )

    private var composition = Decal(
        name = "static-shot-composition",
        sheets = super.sheets,
        position = Position(
            left = 0f,
            bottom = -1600f
        )
    )

    private val compositionShot = StaticShot(
        caption = "Composition",
        factory = shotFactory,
        left = 0f,
        bottom = -1600f,
        dimension = 1936f,
        type = ShotDimension.WIDTH,
        duration = 10f
    )

    init {
        initialShot = overviewShot
    }

    init {
        manageContent(
            instructions,
            overviewShot,
            dialogA,
            southParkLipsDecal,
            dialogBDecal,
            dialogAShot,
            dialogBShot,
            composition,
            compositionShot
        )
        
        input[Input.Keys.NUM_1] = {
            instructions.visible = false
            dialogAShot.debuggable = false
            dialogAShot.cut()
        }
        input[Input.Keys.NUM_3] = {
            instructions.visible = false
            compositionShot.debuggable = false
            compositionShot.cut()
        }
        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        overviewShot.update(dt)
        dialogAShot.update(dt)
        dialogBShot.update(dt)
        compositionShot.update(dt)
        southParkLipsDecal.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        dialogA.render(batch)
        dialogBDecal.render(batch)
        composition.render(batch)
        southParkLipsDecal.render(batch)

        dialogAShot.render(batch)
        dialogBShot.render(batch)
        overviewShot.render(batch)
        compositionShot.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }


}