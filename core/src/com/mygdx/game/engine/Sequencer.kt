package com.mygdx.game.engine

import com.mygdx.game.engine.stdx.Update
import kotlin.math.max
import kotlin.math.min

class Sequencer(
    val duration: Float,
    private val onStart: () -> Unit = { },
    private val onDone: () -> Unit = { },
    private val onUpdate: (dt: Float, elapsed: Float, progress: Float) -> Unit = { _, _, _ -> }
): Update {
    private var running = false
    var elapsed: Float = 0f
    var progress: Float = 0f
        get() { return max(0f, min(elapsed / duration, 1f)) }

    val isRunning: Boolean
        get() { return running }

    val isNotRunning: Boolean
        get() { return !isRunning }

    fun start() {
        if (!running) {
            elapsed = 0f
            running = true
            onStart()
        }
    }

    override fun update(dt: Float) {
        if (running) {
            if (elapsed > duration) {
                running = false
                elapsed = 0f
                onDone()
            } else {
                elapsed += dt
                onUpdate(dt, elapsed, progress)
            }
        }
    }
}