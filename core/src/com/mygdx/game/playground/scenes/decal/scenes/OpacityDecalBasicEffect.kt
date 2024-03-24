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

class OpacityDecalBasicEffect(
    scene: Scene,
    cameraController: CameraController,
    spriteManager: () -> SpriteSheetManager,
    globalInstructions: Label
): GameScene("Opacity Decal Basic Effect") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Decal Basic Effects",
        left = 750f + 3000f, bottom = -100f,
        dimension = 1400f, type = PanelDimension.HEIGHT
    )

    private var tobi = Decal(
        name = "tobi-idle",
        iterations = infinite(),
        sheets = spriteManager,
        position = Position(
            left = 4688f,
            bottom = 420f
        )
    )

    private val localInstructions = Label(
        "Opacity\n" +
        "[1] none\n" +
        "[2] low\n" +
        "[3] medium\n" +
        "[4] high\n" +
        "[5] full\n\n" +
        "Blend\n" +
        "[6] on\n" +
        "[7] off\n\n" +
        "[X] go back",
        Color.BLACK,
        4088f, 1100f,
        visible = false
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        tobi,
        localInstructions
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
                        globalInstructions,
                        localInstructions,
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
        localInstructions.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        tobi.render(batch)
        firstPanel.render(batch)
        localInstructions.render(batch)
    }

    class OpacitySequence(
        val cameraController: CameraController,
        private val devPanel: Panel,
        private val globalInstructions: Label,
        private val localInstructions: Label,
        private val tobi: Decal
    ): Sequence() {

        override val duration: Float = Float.POSITIVE_INFINITY


        override fun onReset() {
            globalInstructions.visible = true
            localInstructions.visible = false
        }

        override fun onStart() {
            globalInstructions.visible = false
            localInstructions.visible = true
            tobi.animiate = true
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                done()
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                tobi.opacity.start(
                    amount = 0f,
                    duration = 0.5f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                tobi.opacity.start(
                    amount = 0.25f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                tobi.opacity.start(
                    amount = 0.5f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                tobi.opacity.start(
                    amount = 0.75f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                tobi.opacity.start(
                    amount = 1.00f,
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
                tobi.blend.on(
                    duration = 1.0f,
                    tween = TweenFunction.EASE_IN_OUT
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_7)) {
                tobi.blend.off(
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

