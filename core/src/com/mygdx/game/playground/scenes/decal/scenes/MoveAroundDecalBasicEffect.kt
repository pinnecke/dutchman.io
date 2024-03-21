package com.mygdx.game.playground.scenes.decal.scenes

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

class MoveAroundDecalBasicEffect(
    scene: Scene,
    cameraController: CameraController,
    spriteManager: () -> SpriteSheetManager,
    instructionHint1: Label,
    instructionHint2: Label
): GameScene("Move Around Decal Basic Effect") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Decal Basic Effects",
        left = 750f, bottom = -100f,
        dimension = 1400f, type = PanelDimension.HEIGHT
    )

    private var tobi1 = Decal(
        name = "tobi-walking",
        iterations = infinite(),
        sheets = spriteManager,
        position = Position(
            left = 2000f,
            bottom = 460f
        )
    )

    private var tobi2 = Decal(
        name = "tobi-idle",
        iterations = once(),
        sheets = spriteManager,
        position = Position(
            left = 1288f,
            bottom = 220f
        ),
        visible = false
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        tobi1,
        tobi2
    )

    override val timeline = Timeline(
        timeLineName = "Decal",
        lanes = listOf(
            Lane(
                name = "Lane 1",
                sequences = listOf(
                    MoveAroundSequence(
                        cameraController,
                        scene.defaultPanel,
                        instructionHint1,
                        instructionHint2,
                        tobi1,
                        tobi2
                    )
                )
            )
        )
    )

    init {
        install(timeline)
    }

    override fun update(dt: Float) {
        tobi1.update(dt)
        tobi2.update(dt)
        firstPanel.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        tobi1.render(batch)
        tobi2.render(batch)
        firstPanel.render(batch)
    }

    class MoveAroundSequence(
        val cameraController: CameraController,
        private val devPanel: Panel,
        private val instructionHint1: Label,
        private val instructionHint2: Label,
        private val tobi1: Decal,
        private val tobi2: Decal
    ): Sequence() {

        override val duration: Float = Float.POSITIVE_INFINITY
        private val startTobie = runDelayed(1f) {
            tobi1.animiate = true
            tobi1.move(
                left = 1300f,
                bottom = 260f,
                duration = 5.5f,
                tween = TweenFunction.EASE_IN_OUT,
                onDone = {
                    tobi1.visible = false
                    tobi2.visible = true
                    tobi2.animiate = true
                    endSequence.start()
                }
            )
        }
        private val endSequence = runTriggered(3f) {
            done()
        }

        override fun onReset() {
            instructionHint1.visible = true
            instructionHint2.visible = true
            tobi1.animiate = false
            tobi2.animiate = false
            tobi1.visible = true
            tobi2.visible = false
            tobi2.reset()
            startTobie.reset()
            endSequence.reset()
            tobi1.move(
                left = 2000f,
                bottom = 460f
            )
        }

        override fun onStart() {
            instructionHint1.visible = false
            instructionHint2.visible = false
            startTobie.start()
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {
            startTobie.update(dt)
            endSequence.update(dt)
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

