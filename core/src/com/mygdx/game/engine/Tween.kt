package com.mygdx.game.engine

import com.mygdx.game.engine.stdx.Update
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

typealias Interpolation = (alpha: Float, start: Float, end: Float) -> Float

enum class TweenFunction(
    val fn: Interpolation
) {
    LINEAR({ alpha, start, end -> start + alpha * (end - start)}),
    EASE_IN( { alpha, start, end -> start + (1f - cos((alpha * Math.PI.toFloat()) / 2f)) * (end - start) } ),
    EASE_OUT( { alpha, start, end -> start + (sin((alpha * Math.PI.toFloat()) / 2f)) * (end - start) } ),
    EASE_IN_OUT( { alpha, start, end -> start + (-(cos(Math.PI.toFloat() * alpha) - 1) / 2) * (end - start) } )
}

data class Tween (
    val duration: Float,
    val origin: () -> Float,
    val target: () -> Float,
    val onInit: () -> Unit = { },
    val onUpdate: (actual: Float) -> Unit,
    val onStart: () -> Unit = { },
    val onDone: () -> Unit = { },
    val interpolate: Interpolation = TweenFunction.LINEAR.fn
): Update {
    private var elapsed = 0f
    private var running = false
    private var x0: Float? = null
    private var x1: Float? = null

    fun start() {
        running = true
        elapsed = 0f
        x0 = origin()
        x1 = target()
        onInit()
        onStart()
    }

    override fun update(dt: Float) {
        if (running) {
            elapsed += dt
            if (elapsed > duration) {
                running = false
                onDone()
            } else {
                val alpha = min(1.0f, max(0.0f, elapsed / duration))
                onUpdate(interpolate(alpha, x0!!, x1!!))
            }
        }
    }

}