package com.mygdx.game.playground.scenes.decal

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene
import com.mygdx.game.playground.scenes.decal.scenes.HighlightDecalBasicEffect
import com.mygdx.game.playground.scenes.decal.scenes.MoveAroundDecalBasicEffect
import com.mygdx.game.playground.scenes.decal.scenes.OpacityDecalBasicEffect

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

    private val instructionHint1 = Label(
        "[1] move around\n" +
       // "[2] start movement path\n" +
       // "[3] masking on/off\n" +
        "[4] opacity\n" +
        "[5] highlight\n" +
        "[6] scale small/normal/high\n" +
        "[7] crash scale on/off\n" +
        "[8] tint none/low/medium/high/full\n" +
        "[9] blur none/low/medium/high",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private val instructionHint2 = Label(
        "[Q] blend in/out\n" +
        "[W] stretch off/horizontal/vertical\n" +
        "[E] compress off/horizontal/vertical\n" +
        "[R] shake on/off up/down/left/right\n" +
        "[T] outline on/off\n" +
        "[SPACE] start/stop animation\n" +
        "[N] next frame\n" +
        "[ESC] back",
        Color.BLACK,
        Engine.canvas.safeZone.left + 750f, Engine.canvas.safeZone.height - 50f,
    )

    private val composer = gameSceneComposerOf(
        composerName = "Timeline Scene",
        cameraController = cameraController,
        autoStart = false,
        initialTimeline = MoveAroundDecalBasicEffect(
            this,
            cameraController,
            this.sheets,
            instructionHint1,
            instructionHint2
        ),
        others = listOf(
            OpacityDecalBasicEffect(
                this,
                cameraController,
                this.sheets,
                instructionHint1,
                instructionHint2
            ),
            HighlightDecalBasicEffect(
                this,
                cameraController,
                this.sheets,
                instructionHint1,
                instructionHint2
            )
        )
    )

    init {
        manageContent(
            instructionHint1,
            instructionHint2,
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
        input[Input.Keys.NUM_4] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(OpacityDecalBasicEffect::class)
                }
            }
        }
        input[Input.Keys.NUM_5] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!composer.isRunning) {
                    composer.start(HighlightDecalBasicEffect::class)
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
        instructionHint1.render(batch)
        instructionHint2.render(batch)
    }

}

