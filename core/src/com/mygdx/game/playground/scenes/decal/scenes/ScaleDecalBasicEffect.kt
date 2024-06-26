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

class ScaleDecalBasicEffect(
    scene: Scene,
    cameraController: CameraController,
    spriteManager: () -> SpriteSheetManager,
    globalInstructions: Label
): GameScene("Scale Decal Basic Effect") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Scale Basic Effects",
        left = 750f + 6000f, bottom = -100f,
        dimension = 1400f, type = PanelDimension.HEIGHT
    )

    private var tobi = Decal(
        name = "tobi-walking",
        iterations = infinite(),
        sheets = spriteManager,
        position = Position(
            left = 7700f,
            bottom = 460f
        )
    )

    private val localInstructions = Label(
        "Scale\n" +
        "[1] small\n" +
        "[2] normal\n" +
        "[3] large\n" +
        "\n\n" +
        "Stretch\n" +
        "[4] small\n" +
        "[5] normal\n" +
        "[6] large\n" +
        "\n\n" +
        "Compress\n" +
        "[7] small\n" +
        "[8] normal\n" +
        "[9] large\n" +
        "\n\n" +
        "[X] go back",
        Color.BLACK,
        4088f + 3000f, 1100f,
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
                    ScaleSequence(
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

    class ScaleSequence(
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
                tobi.scale.start(
                    amount = 0.5f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                tobi.scale.start(
                    amount = 1.0f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                tobi.scale.start(
                    amount = 2.0f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                tobi.stretch.start(
                    amount = 0.5f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                tobi.stretch.start(
                    amount = 1.0f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
                tobi.stretch.start(
                    amount = 2.0f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_7)) {
                tobi.compress.start(
                    amount = 0.5f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_8)) {
                tobi.compress.start(
                    amount = 1.0f,
                    duration = 1.0f
                )
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_9)) {
                tobi.compress.start(
                    amount = 2.0f,
                    duration = 1.0f
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

