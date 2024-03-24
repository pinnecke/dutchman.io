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

class ShakeDecalBasicEffect(
    scene: Scene,
    cameraController: CameraController,
    spriteManager: () -> SpriteSheetManager,
    globalInstructions: Label
): GameScene("Shake Decal Basic Effect") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Decal Basic Effects",
        left = 750f + 9000f, bottom = -100f,
        dimension = 1400f, type = PanelDimension.HEIGHT
    )

    private var tobi = Decal(
        name = "tobi-idle",
        iterations = infinite(),
        sheets = spriteManager,
        position = Position(
            left = 10700f,
            bottom = 460f
        )
    )

    private val localInstructions = Label(
        "Shake Effect\n" +
        "[SPACE] on/off\n" +
        "\n\n" +
        "Horizontal Shake Amount\n" +
        "[1] none\n" +
        "[2] medium\n" +
        "[3] high\n" +
        "\n\n" +
        "Vertical Shake Amount\n" +
        "[4] none\n" +
        "[5] positive medium\n" +
        "[6] positive high\n" +
        "[7] negative medium\n" +
        "[8] negative high\n" +
        "\n\n" +
        "[X] go back",
        Color.BLACK,
        4088f + 6000f, 1100f,
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
                    ShakeSequence(
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

    class ShakeSequence(
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
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                done()
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                tobi.shake.horizontal = 0.0f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                tobi.shake.horizontal = 0.5f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                tobi.shake.horizontal = 1.0f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                tobi.shake.vertical = 0.0f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                tobi.shake.vertical = 0.5f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
                tobi.shake.vertical = 1.0f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
                tobi.shake.vertical = -0.5f
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
                tobi.shake.vertical = -1.0f
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (tobi.shake.isNotRunning) {
                    tobi.shake.start()
                } else {
                    tobi.shake.stop()
                }
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

