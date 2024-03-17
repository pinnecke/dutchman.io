package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.stdx.value

class StaticShotCameraMovement(
    scene: Scene,
    private val cameraController: CameraController,
    devPanel: Panel,
    instructionHint: Label
): GameScene("Static Shot") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    private var shotFrame = Decal(
        name = "camera-shot-static",
        sheets = scene.sheets,
        position = Position(
            left = 0f,
            bottom = 0f
        )
    )

    override val firstPanel = panelOf(
        caption = "Static Shot",
        left = 0f, bottom = 0f,
        dimension = 1103f, type = PanelDimension.HEIGHT
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        shotFrame
    )

    override val timeline = Timeline(
        timeLineName = "Static Shot",
        lanes = listOf(
            Lane(
                name = "Lane 1",
                sequences = listOf(
                    Sequence1(
                        cameraController,
                        firstPanel,
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
        firstPanel.update(dt)
        shotFrame.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        shotFrame.render(batch)
        firstPanel.render(batch)
    }

    class Sequence1(
        val cameraController: CameraController,
        val firstPanel: Panel,
        val instructionHint: Label,
        val devPanel: Panel
    ): Sequence() {

        override val duration: Float = 5.0f

        override fun onReset() {
            firstPanel.visible = true
            instructionHint.visible = true
        }

        override fun onStart() {
            firstPanel.visible = false
            instructionHint.visible = false
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {

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

