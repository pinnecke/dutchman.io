package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.Sequence
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.shots.PanTiltShot
import com.mygdx.game.engine.shots.StaticShot
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class TimelineScene: Scene(
    clearColor = Color.GRAY,
) {
    private val sequence = SequenceController(this)
    private val input = GdxKeyboardInputUtil()
    private val inputDelayer = Sequencer(0.1f, onStart = { println("Wait") }, onDone = { println("Next") })

    private val instructionHint = Label(
        "[1] Timeline 1\n" +
             "[2] Timeline 2\n" +
             "[SPACE] start/pause/resume \n" +
             "[S] stop \n" +
             "[BACKSPACE] rewind \n" +
             "[ESC] back\n\n",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private val selectionHint = Label(
        "Timeline 1",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 260f,
    )

    private val master = TimelineMaster(
        componentManager = sceneComponentManager
    )
    private val timelineA = TimelineA(master)



    private val timeline2 = Timeline(
        master = master,
        lanes = listOf(),
        onStart = {
            println("Timeline 2 started")
        },
        onUpdate = { dt, alpha, elapsed, total ->
            println("Timeline 2 $dt, $alpha, $elapsed, $total")
        },
        onPause = {
            println("Timeline 2 paused")
        },
        onStop = {
            println("Timeline 2 stopped")
        },
        onRewind = {
            println("Timeline 2 rewind")
        },
        onDone = {
            println("Timeline 2 done")
        }
    )
    private var selection: Timeline = timelineA.timeline

    private val overviewShot = StaticShot(
        caption = "Developer View",
        factory = shotFactory,
        scene = this,
        left = -2000f,
        bottom = -1500f,
        dimension = 8000f,
        type = ShotDimension.WIDTH,
        duration = Float.POSITIVE_INFINITY
    )

    private var widescreenFrame = FrameAnimation(
        name = "pan-shot-widescreen",
        sheets = super.sheets
    )

    private val widescreenShot = PanTiltShot(
        caption = "Example",
        factory = shotFactory,
        scene = this,
        beginLeft = 200f,
        beginBottom = 200f,
        beginDimension = 1150f,
        beginType = ShotDimension.HEIGHT,
        endLeft = 1620f,
        endBottom = 200f,
        endDimension = 1150f,
        endType = ShotDimension.HEIGHT,
        duration = 2f,
        onUpdates = { _,_,_ -> println("Done") }
    )

    init {
        initialShot = overviewShot
    }

    override fun create() {
        selectionHint.create()
        selectionHint.visible = true
        instructionHint.create()

        master.create()

        widescreenFrame.create()

        widescreenShot.create()
        overviewShot.create()

        input[Input.Keys.NUM_1] = {
            selectionHint.text = "Timeline 1"
            selection = timelineA.timeline
        }
        input[Input.Keys.NUM_2] = {
            selectionHint.text = "Timeline 2"
            selection = timeline2
        }
        input[Input.Keys.SPACE] = {
            if (inputDelayer.isNotRunning) {
                inputDelayer.start()
                if (!master.isRunning) {
                    master.start(selection)
                } else if (master.isRunning && master.isPaused) {
                    master.resume(selection)
                } else if (master.isRunning && !master.isPaused) {
                    master.pause(selection)
                }
            }
        }
        input[Input.Keys.S] = {
            master.stop(selection)
        }
        input[Input.Keys.BACKSPACE] = {
            master.rewind(selection)
        }

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun update(dt: Float) {
        input.act()
        master.update(dt)
        inputDelayer.update(dt)

        widescreenShot.update(dt)
        overviewShot.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        master.render(batch)
        widescreenFrame.render(batch)

        widescreenShot.render(batch)
        overviewShot.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        selectionHint.render(batch)
        instructionHint.render(batch)
    }

    override fun destroy(){
        master.destroy()
        selectionHint.destroy()
        instructionHint.destroy()

        widescreenFrame.destroy()

        overviewShot.destroy()
        widescreenShot.destroy()
    }

}

class TimelineA(master: TimelineMaster): ManagedObjects {

    override fun create() {
        println("Timeline 1 objects: created")
    }

    override fun destroy() {
        println("Timeline 1 objects: destroyed")
    }

    val timeline = Timeline(
        master = master,
        lanes = listOf(
            Lane(
                objects = this,
                sequences = listOf(
                    SequencePlayer(
                        sequence = object: Sequence {
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