package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent

class GameScene2: GameScene("Game Scene 2") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Initial Panel",
        left = 2000f, bottom = 0f,
        dimension = Engine.canvas.surface.width, type = PanelDimension.WIDTH
    )

    override val timeline = Timeline(
        timeLineName = "Game Scene 2 Timeline",
        lanes = listOf(
            Lane(
                name = "Game Scene 2 Timeline Lane 1",
                sequences = listOf(
                    Sequence1()
                )
            )
        )
    )

    override val managedContent = mutableListOf<ManagedContent>(
        timeline,
        firstPanel
    )

    override fun update(dt: Float) {
        firstPanel.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        firstPanel.render(batch)
    }

    class Sequence1: Sequence() {

        override val duration: Float = 5.0f

        override fun onReset() {
            println("Timeline 1 sequence 1: rewind")
        }

        override fun onStart() {
            println("Timeline 1 sequence 1: start")
        }

        override fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float) {
            println("Timeline 1 sequence 1: update $dt, $alpha, $elapsed, $total")
        }

        override fun onRender(batch: SpriteBatch) {
            println("Timeline 1 sequence 1: render")
        }

        override fun onDone() {
            println("Timeline 1 sequence 1: done")
        }
    }
}
