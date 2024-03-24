package com.mygdx.game.engine

import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.Update
import kotlin.math.sin

class HorizontalShakeAnimation(
    name: String,
    private val settings: Settings
): Animator (
    name = "Horizontal Shake ($name)",
    function = { (settings.amount * sin(settings.speed * it)) },
    rampUp = RampEffect(
        duration = settings.rampUp,
        tween = TweenFunction.EASE_IN_OUT
    ),
    rampDown = RampEffect(
        duration = settings.rampDown,
        tween = TweenFunction.EASE_IN_OUT
    )
) {
    data class Settings (
        val speed: Float = 10f,
        val amount: Float = 50f,
        val rampUp: Float = 1f,
        val rampDown: Float = 1f,
        val horizontalAmount: Float = 1f,
        val verticalAmount: Float = 0f,
        val horizontalChangeTime: Float = 1f,
        val verticalChangeTime: Float = 1f,
    )

    override val id: String = "Horizontal Shake Animation"

    private val horizontalAmount = Tween(
        id = "horizontal ($name)",
        create = { settings.horizontalAmount }
    )

    private val verticalAmount = Tween(
        id = "horizontal ($name)",
        create = { settings.horizontalAmount }
    )

    var horizontal: Float
        get() { return horizontalAmount.amount * super.alpha }
        set(value) {
            horizontalAmount.start(
                amount = value,
                duration = settings.horizontalChangeTime
            )
        }

    var vertical: Float
        get() { return verticalAmount.amount * super.alpha }
        set(value) {
            verticalAmount.start(
                amount = value,
                duration = settings.horizontalChangeTime
            )
        }

    override fun loadContent() {
        super.loadContent()
        horizontalAmount.loadContent()
        verticalAmount.loadContent()
        horizontal = settings.horizontalAmount
        vertical = settings.verticalAmount
    }

    override fun unloadContent() {
        super.unloadContent()
        horizontalAmount.unloadContent()
        verticalAmount.unloadContent()
    }

    override fun update(dt: Float) {
        super.update(dt)
        horizontalAmount.update(dt)
        verticalAmount.update(dt)
    }

}

data class RampEffect(
    val duration: Float,
    val tween: TweenFunction
) {
    companion object {
        fun none() = RampEffect(
            duration = 0f,
            tween = TweenFunction.LINEAR
        )
    }
}

open class Animator(
    name: String,
    private val function: (x: Float) -> Float,
    private val rampUp: RampEffect = RampEffect.none(),
    private val rampDown: RampEffect = RampEffect.none(),
): ManagedContent, Update {

    enum class AnimationState {
        STOPPED,
        RAMP_UP,
        RUNNING,
        RAMP_DOWN
    }

    private var elapsed = 0f
    private var x = 0f
    private var strength = 0f
    private var running = false
    private var state = AnimationState.STOPPED

    private var rampUpTween = Tween(
        id = "Ramp Up ($name)",
        create = { this },
        init = { this.strength },
        configure = { self, amount ->
            self.strength = amount
            println("Ramping up: ${self.strength}")
        }
    )

    private var rampDownTween = Tween(
        id = "Ramp Up ($name)",
        create = { this },
        init = { this.strength },
        configure = { self, amount ->
            self.strength = amount
            println("Ramping down: ${self.strength}")
        }
    )

    override val id = "Animator (${name})"

    val alpha: Float
        get() { return strength * x }

    val isRunning: Boolean
        get() { return state == AnimationState.RUNNING || state == AnimationState.RAMP_UP }

    val isNotRunning: Boolean
        get() { return state == AnimationState.STOPPED || state == AnimationState.RAMP_DOWN }

    override fun loadContent() {
        rampUpTween.loadContent()
        rampDownTween.loadContent()
        elapsed = 0f
        x = 0f
    }

    override fun unloadContent() {
        rampUpTween.unloadContent()
        rampDownTween.unloadContent()
    }

    override fun update(dt: Float) {
        if (running) {
            elapsed += dt
            x = function(elapsed)
            rampUpTween.update(dt)
            rampDownTween.update(dt)
        }
    }

    fun start() {
        if (state == AnimationState.STOPPED || state == AnimationState.RAMP_DOWN) {
            running = true
            rampDownTween.stop()
            rampDownTween.reset()
            rampUpTween.reset()

            rampUpTween.start(
                amount = 1f,
                duration = rampUp.duration,
                tween = rampUp.tween,
                onStart = {
                    state = AnimationState.RAMP_UP
                },
                onDone = {
                    state = AnimationState.RUNNING
                }
            )
        }
    }

    fun stop() {
        if (state == AnimationState.RUNNING || state == AnimationState.RAMP_UP) {
            rampUpTween.stop()
            rampUpTween.reset()
            rampDownTween.reset()

            rampDownTween.start(
                amount = 0f,
                duration = rampDown.duration,
                tween = rampDown.tween,
                onStart = {
                    state = AnimationState.RAMP_DOWN
                },
                onDone = {
                    state = AnimationState.STOPPED
                    running = false
                }
            )
        }
    }

}