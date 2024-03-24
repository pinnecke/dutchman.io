package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position

class PanShotCameraMovement(
    scene: Scene,
    private val cameraController: CameraController,
    devPanel: Panel,
    instructionHint: Label
): GameScene("Pan Shot") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    private var leftFrame = Decal(
        name = "camera-shot-pan-left",
        sheets = scene.sheets,
        position = Position(
            left = 0f,
            bottom = 0f
        )
    )
    private var rightFrame = Decal(
        name = "camera-shot-pan-right",
        sheets = scene.sheets,
        position = Position(
            left = 2048f,
            bottom = 0f
        )
    )

    override val firstPanel = panelOf(
        caption = "Pan Shot (Start)",
        left = 0f, bottom = 0f,
        dimension = 1900f, type = PanelDimension.HEIGHT
    )


    val panEndPanel = panelOf(
        caption = "Pan Shot (End)",
        left = 1048f, bottom = 0f,
        dimension = 1900f, type = PanelDimension.HEIGHT
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        leftFrame,
        rightFrame,
        panEndPanel
    )

    override val timeline = Timeline(
        timeLineName = "Pan Shot",
        lanes = listOf(
            Lane(
                name = "Lane 1",
                sequences = listOf(
                    Sequence1(
                        cameraController,
                        firstPanel,
                        panEndPanel,
                        instructionHint,
                        devPanel
                    )
                )
            )
        )
    )

    init {
        install(timeline)
    }

    override fun update(dt: Float) {
        leftFrame.update(dt)
        rightFrame.update(dt)

        firstPanel.update(dt)
        panEndPanel.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        leftFrame.render(batch)
        rightFrame.render(batch)

        firstPanel.render(batch)
        panEndPanel.render(batch)
    }

    class Sequence1(
        val cameraController: CameraController,
        val panBeginPanel: Panel,
        val panEndPanel: Panel,
        val instructionHint: Label,
        val devPanel: Panel
    ): Sequence() {

        override val duration: Float = 8.0f
        private var panStarted: Boolean = false

        override fun onReset() {
            panBeginPanel.visible = true
            panEndPanel.visible = true
            instructionHint.visible = true
            panStarted = false
        }

        override fun onStart() {
            panBeginPanel.visible = false
            panEndPanel.visible = false
            instructionHint.visible = false
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {
            if (elapsed > 1.5f && !panStarted) {
                cameraController.camera.cut(
                    panel = panEndPanel,
                    effect = CutEffectDescriptor.smooth(6f)
                )
                panStarted = true
            }
        }

        override fun onRender(batch: SpriteBatch) {

        }

        override fun onDone() {
            cameraController.camera.cut(
                devPanel,
                CutEffectDescriptor.smooth(1f)
            )
            onReset()
        }
    }
}

