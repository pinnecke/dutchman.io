package com.mygdx.game.playground.scenes.timeline

import com.mygdx.game.engine.*

class GameScene1(master: TimelineMaster): GameScene {

    override fun create() {
        println("Timeline 1 objects: created")
    }

    override fun destroy() {
        println("Timeline 1 objects: destroyed")
    }

    override val timeline = Timeline(
        master = master,
        lanes = listOf(
            Lane(
                objects = this,
                sequences = listOf(
                    SequencePlayer(
                        sequence = object : Sequence {
                            override fun update(dt: Float, alpha: Float, elapsed: Float, totalTime: Float) {
                                println("Timeline 1 sequence 1: update $dt, $alpha, $elapsed, $totalTime")
                            }

                            override fun done() {
                                println("Timeline 1 sequence 1: done")
                            }

                            override fun start() {
                                println("Timeline 1 sequence 1: start")
                            }

                            override fun pause() {
                                println("Timeline 1 sequence 1: pause")
                            }

                            override fun resume() {
                                println("Timeline 1 sequence 1: resume")
                            }

                            override fun stop() {
                                println("Timeline 1 sequence 1: stop")
                            }

                            override fun rewind() {
                                println("Timeline 1 sequence 1: rewind")
                            }
                        },
                        duration = 5f
                    )
                ),
                onStart = { println("Timeline 1 lanes started") },
                onUpdate = { dt, alpha, elapsed, total -> println("Timeline 1 lanes update $dt, $alpha, $elapsed, $total") },
                onDone = { println("Timeline 1 lanes ended") }
            )
        ),
        onStart = {
            println("Timeline 1 started")
        },
        onUpdate = { dt, alpha, elapsed, total ->
            println("Timeline 1 $dt, $alpha, $elapsed, $total")
        },
        onPause = {
            println("Timeline 1 paused")
        },
        onStop = {
            println("Timeline 1 stopped")
        },
        onRewind = {
            println("Timeline 1 rewind")
        },
        onDone = {
            println("Timeline 1 done")
        }
    )

}