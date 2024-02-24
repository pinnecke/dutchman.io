package com.mygdx.game.engine

private object EmptyAction: StepAction {
    override var hasFinished: Boolean = true
    override fun start() { }
    override fun step() { }
}

fun emptyStepAction(): StepAction = EmptyAction

fun stepwise(
    boot: () -> Unit,
    vararg steps: () -> Unit
) = object: StepAction {
    private var stepIdx = 0
    override var hasFinished: Boolean = stepIdx == steps.size

    override fun start() = boot()
    override fun step() = steps[stepIdx++]()
}

interface StepAction {
    var hasFinished: Boolean
    fun start()
    fun step()
}