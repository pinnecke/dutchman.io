package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.objects.Label

class GameScene1: GameScene("Game Scene 1") {

    override val cutInEffect = CutEffectDescriptor.smooth(1f)

    override val firstPanel = panelOf(
        caption = "Initial Panel",
        left = 0f, bottom = 0f,
        dimension = Engine.canvas.surface.width, type = PanelDimension.WIDTH
    )

    private val thisIsScene1Label = Label(
        "This is scene 1",
        textColor = Color.RED,
        x = 400f, y = 400f
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel,
        thisIsScene1Label
    )

    override val timeline = Timeline(
        timeLineName = "Game Scene 1 Timeline",
        lanes = listOf(
            Lane(
                name = "Game Scene 1 Timeline Lane 1",
                sequences = listOf(
                    Sequence1()
                )
            )
        )
    )

    init {
        install(timeline)
    }

    override fun update(dt: Float) {
        firstPanel.update(dt)
        thisIsScene1Label.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        firstPanel.render(batch)
        thisIsScene1Label.render(batch)
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

