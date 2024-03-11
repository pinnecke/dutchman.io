package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.stdx.value

class WhipPanShotCameraMovement(
    scene: Scene,
    private val cameraController: CameraController,
    devPanel: Panel,
    instructionHint: Label
): GameScene("Whip Pan Shot") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    private var frame0 = FrameAnimation(
        name = "camera-shot-whip-pan-0",
        sheets = scene.sheets,
        position = value(Position(
            left = { 0 * 2048f },
            bottom = { 0f }
        ))
    )
    private var frame1 = FrameAnimation(
        name = "camera-shot-whip-pan-1",
        sheets = scene.sheets,
        position = value(Position(
            left = { 1 * 2048f },
            bottom = { 0f }
        ))
    )
    private var frame2 = FrameAnimation(
        name = "camera-shot-whip-pan-2",
        sheets = scene.sheets,
        position = value(Position(
            left = { 2 * 2048f },
            bottom = { 0f }
        ))
    )
    private var frame3 = FrameAnimation(
        name = "camera-shot-whip-pan-3",
        sheets = scene.sheets,
        position = value(Position(
            left = { 3 * 2048f },
            bottom = { 0f }
        ))
    )

    override val firstPanel = panelOf(
        caption = "Whip Pan Shot (Start)",
        left = 0f, bottom = 0f,
        dimension = 1900f, type = PanelDimension.HEIGHT
    )


    val panEndPanel = panelOf(
        caption = "Whip Pan Shot (End)",
        left = 5 * 1048f, bottom = 0f,
        dimension = 1900f, type = PanelDimension.HEIGHT
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        frame0,
        frame1,
        frame2,
        frame3,
        panEndPanel
    )

    override val timeline = Timeline(
        timeLineName = "Whip Pan Shot",
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
        frame0.update(dt)
        frame1.update(dt)
        frame2.update(dt)
        frame3.update(dt)

        firstPanel.update(dt)
        panEndPanel.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        frame0.render(batch)
        frame1.render(batch)
        frame2.render(batch)
        frame3.render(batch)

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

        override val duration: Float = 5.0f
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
                    effect = CutEffectDescriptor.smooth(0.3f)
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

