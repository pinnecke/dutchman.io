package com.mygdx.game.engine

import com.mygdx.game.engine.stdx.Update
import java.lang.Math.cos
import kotlin.math.cos

typealias Interpolation = (alpha: Float, start: Float, end: Float) -> Float

enum class TweenFunction(
    val fn: Interpolation
) {
    LINEAR({ alpha, start, end -> start + alpha * (end - start)}),
    EASE_IN( { alpha, start, end -> start + (1f - cos((alpha * Math.PI.toFloat()) / 2f)) * (end - start) } ),
    EASE_IN_OUT( { alpha, start, end -> start + (-(cos(Math.PI.toFloat() * alpha) - 1) / 2) * (end - start) } )
}

data class Tween (
    val duration: Float,
    val origin: () -> Float,
    val target: () -> Float,
    val onInit: () -> Unit,
    val onUpdate: (actual: Float) -> Unit,
    val onStart: () -> Unit = { },
    val onIterationEnd: (iteration: Int) -> Unit = { },
    val onExecutionEnd: () -> Unit = { },
    val interpolate: Interpolation = TweenFunction.LINEAR.fn,
    val iterations: Int = 1
): Update {
    private var elapsed = 0f
    private var running = false
    private var iteration = 0
    private var x0: Float? = null
    private var x1: Float? = null

    fun start() {
        running = true
        elapsed = 0f
        iteration = 0
        x0 = origin()
        x1 = target()
        onInit()
        onStart()
    }

    override fun update(dt: Float) {
        if (running) {
            elapsed += dt
            val alpha = elapsed / duration
            onUpdate(interpolate(alpha, x0!!, x1!!))
            if (elapsed > duration) {
                onIterationEnd(iteration)
                if (++iteration == iterations) {
                    running = false
                    onExecutionEnd()
                }
            }
        }
    }

}