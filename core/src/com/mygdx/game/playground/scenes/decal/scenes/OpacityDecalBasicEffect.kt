package com.mygdx.game.playground.scenes.decal.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.stdx.infinite
import com.mygdx.game.engine.stdx.once
import com.mygdx.game.engine.stdx.runDelayed
import com.mygdx.game.engine.stdx.runTriggered

class OpacityDecalBasicEffect(
    scene: Scene,
    cameraController: CameraController,
    spriteManager: () -> SpriteSheetManager,
    instructionHint1: Label,
    instructionHint2: Label
): GameScene("Opacity Decal Basic Effect") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Decal Basic Effects",
        left = 750f + 3000f, bottom = -100f,
        dimension = 1400f, type = PanelDimension.HEIGHT
    )

    private var tobi = Decal(
        name = "tobi-idle",
        scale = 1.0f,
        iterations = infinite(),
        sheets = spriteManager,
        position = Position(
            left = 4688f,
            bottom = 420f
        )
    )

    private val goBackHint = Label(
        "Opacity\n" +
        "[1] none\n" +
        "[2] low\n" +
        "[3] medium\n" +
        "[4] high\n" +
        "[5] full\n\n" +
        "[X] go back",
        Color.BLACK,
        4088f, 1100f,
        visible = false
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        tobi,
        goBackHint
    )

    override val timeline = Timeline(
        timeLineName = "Decal",
        lanes = listOf(
            Lane(
                name = "Lane 1",
                sequences = listOf(
                    OpacitySequence(
                        cameraController,
                        scene.defaultPanel,
                        instructionHint1,
                        instructionHint2,
                        goBackHint,
                        tobi
                    )
                )
            )
        )
    )

    init {
        install(timeline)
    }

    override fun update(dt: Float) {
        tobi.update(dt)
        firstPanel.update(dt)
        goBackHint.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        tobi.render(batch)
        firstPanel.render(batch)
        goBackHint.render(batch)
    }

    class OpacitySequence(
        val cameraController: CameraController,
        private val devPanel: Panel,
        private val instructionHint1: Label,
        private val instructionHint2: Label,
        private val instructionHint3: Label,
        private val tobi: Decal
    ): Sequence() {

        override val duration: Float = Float.POSITIVE_INFINITY


        override fun onReset() {
            instructionHint1.visible = true
            instructionHint2.visible = true
            instructionHint3.visible = false
        }

        override fun onStart() {
            instructionHint1.visible = false
            instructionHint2.visible = false
            instructionHint3.visible = true
            tobi.animiate = true
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                done()
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                tobi.opacity(
                    amount = 0f,
                    duration = 0.5f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                tobi.opacity(
                    amount = 0.25f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                tobi.opacity(
                    amount = 0.5f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                tobi.opacity(
                    amount = 0.75f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                tobi.opacity(
                    amount = 1.00f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
        }

        override fun onRender(batch: SpriteBatch) {

        }

        override fun onDone() {
            cameraController.camera.cut(
                devPanel,
                CutEffectDescriptor.smooth(1f),
                onDone = {
                    onReset()
                }
            )
        }
    }
}

