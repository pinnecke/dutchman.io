package com.mygdx.game.engine

import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.Update
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private const val MINIMUM_AMOUNT = 0.0001f

class Tweenable<T>(
    override val id: String,
    private val create: () -> T,
    private val enable: (obj: T) -> Unit,
    private val disable: (obj: T) -> Unit,
    private val destroy: (obj: T) -> Unit,
    private val configure: (obj: T, amount: Float) -> Unit,
    init: Float = MINIMUM_AMOUNT
): ManagedContent, Update {

    private var tween: Tween? = null
    private var currentAmount = init
    private var targetAmount = 0f
    private var obj: T? = null

    val amount: Float
        get() { return currentAmount }

    override fun loadContent() {
        obj = create()
        enable(obj!!)
        configure(obj!!, currentAmount)
        disable(obj!!)
    }

    override fun unloadContent() {
        destroy(obj!!)
    }

    override fun update(dt: Float) {
        tween?.update(dt)
        if (currentAmount != targetAmount) {
            configure(obj!!, currentAmount)
        }
    }

    var enabled: Boolean = false
        set(value) { if (value != field) { if (value) { enable(obj!!) } else { disable(obj!!) }; field = value } }

    fun configure(
        amount: Float,
        duration: Float,
        tweenFunction: TweenFunction = TweenFunction.EASE_IN_OUT,
        onDone: () -> Unit = { }
    ) {
        tween = Tween(
            duration = duration,
            onInit = {
                targetAmount = max(MINIMUM_AMOUNT, amount)
                enabled = true
            },
            origin = { currentAmount },
            target = { amount },
            onUpdate = { currentAmount = it },
            interpolate = tweenFunction.fn,
            onDone = {
                targetAmount = currentAmount
                onDone()
            }
        )
        tween?.start()
    }
}

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
    val onInit: () -> Unit = { },
    val origin: () -> Float,
    val target: () -> Float,
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
        onInit()
        x0 = origin()
        x1 = target()
        onStart()
    }

    override fun update(dt: Float) {
        if (running) {
            if (elapsed > duration) {
                running = false
                onDone()
            } else {
                if (dt > duration) {
                    onUpdate(x1!!)
                } else {
                    val alpha = min(1.0f, max(0.0f, elapsed / duration))
                    onUpdate(interpolate(alpha, x0!!, x1!!))
                }

            }
            elapsed += dt
        }
    }

}