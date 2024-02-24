package com.mygdx.game.engine.stdx

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
    private var running = !triggered

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