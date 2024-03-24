package com.mygdx.game.engine

import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.Update
import kotlin.math.*

private const val ALMOST_ZERO = 0.0001f

interface Tweenable: Update {

    var enabled: Boolean
    val amount: Float
    val target: Float
    val isTweening: Boolean

    val isNotTweening: Boolean
        get() = !isTweening

    fun start(
        amount: Float,
        duration: Float,
        tween: TweenFunction = TweenFunction.EASE_IN_OUT,
        onStart: () -> Unit = { },
        onDone: () -> Unit = { }
    )

    fun stop()

}

fun Float.approxEqual(other: Float): Boolean = abs(this - other) < 0.0001

class TweakFlagMultiplexer<T> (
    private val on: Float,
    private val off: Float,
    vararg val tweens: Tween<T>
): Update {
    private var onDoneCallback: (() -> Unit)? = null

    var enabled: Boolean
        get() {
            return tweens.map { enabled }.reduceRight { lhs, rhs -> lhs && rhs }
        }
        set(value) {
            tweens.forEach { it.enabled = value }
        }

    val isOn: Boolean
        get() {
            return tweens.first().amount.approxEqual(on)
        }

    val isOff: Boolean
        get() {
            return tweens.first().amount.approxEqual(off)
        }

    val isTweening: Boolean
        get() {
            return tweens.first().isTweening
        }

    fun on(duration: Float, tween: TweenFunction, onDone: () -> Unit = { }) {
        tweens.forEach {
            it.start(on, duration, tween) { }
        }
        onDoneCallback = onDone
    }

    fun off(duration: Float, tween: TweenFunction, onDone: () -> Unit = { }) {
        tweens.forEach {
            it.start(off, duration, tween) { }
        }
        onDoneCallback = onDone
    }

    override fun update(dt: Float) {
        tweens.forEach { it.update(dt) }
        if (!isTweening && onDoneCallback != null) {
            onDoneCallback!!()
            onDoneCallback = null
        }
    }
}

class TweenMultiplexer<T>(
    vararg val tweens: Tween<T>
): Tweenable {

    private var onDoneCallback: (() -> Unit)? = null

    override var enabled: Boolean
        get() {
            return tweens.map { enabled }.reduceRight { lhs, rhs -> lhs && rhs }
        }
        set(value) {
            tweens.forEach { it.enabled = value }
        }

    override val amount: Float
        get() {
            return tweens.first().amount
        }

    override val target: Float
        get() {
            return tweens.first().target
        }

    override val isTweening: Boolean
        get() {
            return tweens.first().isTweening
        }

    override fun start(
        amount: Float, duration: Float,
        tween: TweenFunction,
        onStart: () -> Unit, onDone: () -> Unit
    ) {
        tweens.forEach {
            it.start(amount, duration, tween) { }
        }
        onDoneCallback = onDone
    }

    override fun stop() = tweens.forEach { it.stop() }

    override fun update(dt: Float) {
        tweens.forEach { it.update(dt) }
        if (!isTweening && onDoneCallback != null) {
            onDoneCallback!!()
            onDoneCallback = null
        }
    }
}

class Tween<T>(
    override val id: String,
    private val create: () -> T,
    private val enable: (obj: T) -> Unit = { },
    private val disable: (obj: T) -> Unit = { },
    private val destroy: (obj: T) -> Unit = { },
    private val configure: (obj: T, amount: Float) -> Unit = { _, _ -> },
    private val init: () -> Float = { ALMOST_ZERO }
): Tweenable, ManagedContent {

    private var tween: TweenProcessor? = null
    private var currentAmount = init()
    private var targetAmount = 0f
    private var obj: T? = null

    override var enabled: Boolean = false
        set(value) { if (value != field) { if (value) { enable(obj!!) } else { disable(obj!!) }; field = value } }

    override val amount: Float
        get() { return currentAmount }

    override val target: Float
        get() { return targetAmount }

    override val isTweening: Boolean
        get() { return tween?.isNotDone ?: false }

    override fun loadContent() {
        obj = create()
        reset()
    }

    override fun unloadContent() {
        destroy(obj!!)
    }

    override fun update(dt: Float) {
        if (tween?.isNotDone == true) {
            tween?.update(dt)
            if (currentAmount != targetAmount) {
                configure(obj!!, currentAmount)
            }
        }
    }

    override fun start(
        amount: Float,
        duration: Float,
        tween: TweenFunction,
        onStart: () -> Unit,
        onDone: () -> Unit
    ) {
        onStart()
        this.tween = TweenProcessor(
            duration = duration,
            onInit = {
                targetAmount = max(ALMOST_ZERO, amount)
                enabled = true
                println("Tween Processor current: $currentAmount, target: $targetAmount")
            },
            origin = { currentAmount },
            target = { amount },
            onUpdate = { currentAmount = it },
            interpolate = tween.fn,
            onDone = {
                targetAmount = currentAmount
                onDone()
            }
        )
        this.tween?.start()
    }

    override fun stop() {
        this.tween?.stop()
    }

    fun reset() {
        currentAmount = init()
        targetAmount = 0f
        enable(obj!!)
        configure(obj!!, currentAmount)
        disable(obj!!)
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

data class TweenProcessor (
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

    val isDone: Boolean
        get() { return !running }

    val isNotDone: Boolean
        get() { return !isDone }

    fun start() {
        running = true
        elapsed = 0f
        onInit()
        x0 = origin()
        x1 = target()
        onStart()
    }

    fun stop() {
        running = false
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