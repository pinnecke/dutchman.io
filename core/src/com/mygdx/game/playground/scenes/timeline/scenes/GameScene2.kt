package com.mygdx.game.playground.scenes.timeline.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.managedContentOf

class GameScene2: GameScene("Game Scene 2") {

    override val managedContent = mutableListOf(
        managedContentOf(
            "Dummy objects",
            load = {
                println("Timeline objects: created")
            },
            unload = {
                println("Timeline objects: destroyed")
            }
        )
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

    init {
        install(timeline)
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
