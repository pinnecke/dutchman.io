package com.mygdx.game.engine

private object EmptyAction: StepAction {
    override var hasFinished: Boolean = true
    override fun start() { }
    override fun step() { }
}

fun emptyStepAction(): StepAction = EmptyAction

interface StepAction {
    var hasFinished: Boolean
    fun start()
    fun step()
}