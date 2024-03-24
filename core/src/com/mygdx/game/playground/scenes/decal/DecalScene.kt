package com.mygdx.game.playground.scenes.decal

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene
import com.mygdx.game.playground.scenes.decal.scenes.MoveAroundDecalBasicEffect
import com.mygdx.game.playground.scenes.decal.scenes.OpacityDecalBasicEffect
import com.mygdx.game.playground.scenes.decal.scenes.ScaleDecalBasicEffect
import com.mygdx.game.playground.scenes.decal.scenes.ShakeDecalBasicEffect

class DecalScene(sceneManager: SceneManager): Scene(
    "Decal Shot",
    sceneManager = sceneManager,
    clearColor = Color.GRAY,
    defaultPanelCaption = "Creator panel",
    defaultPanelLeft = -1000f,
    defaultPanelBottom = -800f,
    defaultPanelDimension = 6100f,
    defaultPanelType = PanelDimension.WIDTH
) {
    private val sequence = SequenceController(this)
    private val cameraController = CameraController(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val usageInstructions = Label(
        "[1] position\n" +
        "[2] opacity\n" +
        "[3] scaling\n" +
        "[4] shake\n",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    override val composer = composerOf(
        composerName = "Timeline Scene",
        cameraController = cameraController,
        autoStart = false,
        initialTimeline = MoveAroundDecalBasicEffect(
            this,
            cameraController,
            this.sheets,
            usageInstructions
        ),
        others = listOf(
            OpacityDecalBasicEffect(
                this,
                cameraController,
                this.sheets,
                usageInstructions
            ),
            ScaleDecalBasicEffect(
                this,
                cameraController,
                this.sheets,
                usageInstructions
            ),
            ShakeDecalBasicEffect(
                this,
                cameraController,
                this.sheets,
                usageInstructions
            )
        )
    )

    init {
        manageContent(
            usageInstructions,
            composer
        )

        input[Input.Keys.NUM_1] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(MoveAroundDecalBasicEffect::class)
                }
            }
        }
        input[Input.Keys.NUM_2] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(OpacityDecalBasicEffect::class)
                }
            }
        }
        input[Input.Keys.NUM_3] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(ScaleDecalBasicEffect::class)
                }
            }
        }
        input[Input.Keys.NUM_4] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(ShakeDecalBasicEffect::class)
                }
            }
        }

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        composer.update(dt)
        inputDelayer.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        composer.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        usageInstructions.render(batch)
    }

}

