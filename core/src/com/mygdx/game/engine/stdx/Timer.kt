package com.mygdx.game.engine.stdx

import kotlin.math.max
import kotlin.math.min

fun Float.seconds(): Float = this
fun Int.seconds(): Float = this.toFloat()

fun runRepeated(
    delay: Float,
    action: () -> Unit
) = Timer(durationSec = delay, timeOutAction = action, runRepeated = true, triggered = false)

fun runDelayed(
    delay: Float,
    action: () -> Unit
) = Timer(durationSec = delay, timeOutAction = action, runRepeated = false, triggered = false)

fun runTriggered(
    delay: Float,
    action: () -> Unit
) = Timer(durationSec = delay, timeOutAction = action, runRepeated = false, triggered = true)

data class Timer(
    val durationSec: Float,
    val timeOutAction: () -> Unit,
    val runRepeated: Boolean,
    val triggered: Boolean
) {
    private var elapsed = 0f
    private var executed = false
    var running = !triggered

    var alpha: Float = 0f
        get() { return max(0f, min(elapsed/durationSec, 1f)) }

    fun reset() {
        elapsed = 0f
        executed = false
        running = !triggered
    }

    fun start() {
        running = true
    }

    fun update(dt: Float) {
        if (running && (!executed || runRepeated)) {
            elapsed += dt
            if (elapsed >= durationSec) {
                timeOutAction()
                executed = true
                elapsed = 0f
                if (!runRepeated) {
                    running = false
                }
            }
        }
    }
}