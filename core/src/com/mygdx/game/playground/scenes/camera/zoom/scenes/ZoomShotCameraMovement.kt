package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.stdx.value

class ZoomShotCameraMovement(
    scene: Scene,
    private val cameraController: CameraController,
    devPanel: Panel,
    instructionHint: Label
): GameScene("Zoom Shot") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    private var frame0 = FrameAnimation(
        name = "camera-shot-zoom-0",
        sheets = scene.sheets,
        position = value(Position(
            left = { 0 * 2000f },
            bottom = { 1 * 2000f }
        ))
    )
    private var frame1 = FrameAnimation(
        name = "camera-shot-zoom-1",
        sheets = scene.sheets,
        position = value(Position(
            left = { 1 * 2000f },
            bottom = { 1 * 2000f }
        ))
    )
    private var frame2 = FrameAnimation(
        name = "camera-shot-zoom-2",
        sheets = scene.sheets,
        position = value(Position(
            left = { 0 * 2000f },
            bottom = { 0 * 2000f }
        ))
    )
    private var frame3 = FrameAnimation(
        name = "camera-shot-zoom-3",
        sheets = scene.sheets,
        position = value(Position(
            left = { 1 * 2000f },
            bottom = { 0 * 2000f }
        ))
    )

    override val firstPanel = panelOf(
        caption = "Zoom Shot (Start)",
        left = -1000f, bottom = 0f,
        dimension = 4000f, type = PanelDimension.HEIGHT
    )


    val zoomEndPanel = panelOf(
        caption = "Zoom Shot (End)",
        left = 0.40f * 4000f, bottom = 0.45f * 4000f,
        dimension = 0.10f * 4000f, type = PanelDimension.HEIGHT
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        frame0,
        frame1,
        frame2,
        frame3,
        zoomEndPanel
    )

    override val timeline = Timeline(
        timeLineName = "Zoom Shot",
        lanes = listOf(
            Lane(
                name = "Lane 1",
                sequences = listOf(
                    Sequence1(
                        cameraController,
                        firstPanel,
                        zoomEndPanel,
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
        zoomEndPanel.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        frame0.render(batch)
        frame1.render(batch)
        frame2.render(batch)
        frame3.render(batch)

        firstPanel.render(batch)
        zoomEndPanel.render(batch)
    }

    class Sequence1(
        val cameraController: CameraController,
        val zoomBeginPanel: Panel,
        val zoomEndPanel: Panel,
        val instructionHint: Label,
        val devPanel: Panel
    ): Sequence() {

        override val duration: Float = 45.0f
        private var panStarted: Boolean = false

        override fun onReset() {
            zoomBeginPanel.visible = true
            zoomEndPanel.visible = true
            instructionHint.visible = true
            panStarted = false
        }

        override fun onStart() {
            zoomBeginPanel.visible = false
            zoomEndPanel.visible = false
            instructionHint.visible = false
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {
            if (elapsed > 1.5f && !panStarted) {
                cameraController.camera.cut(
                    panel = zoomEndPanel,
                    effect = CutEffectDescriptor.easeIn(43.0f)
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

