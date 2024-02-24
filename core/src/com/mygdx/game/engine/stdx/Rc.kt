package com.mygdx.game.engine.stdx

class Rc<T, K>(
    val key: K,
    val value: T,
    private val free: (self: Rc<T, K>) -> Unit,
    private var counter: Int = 0
) {
    internal fun acquire() {
        counter++
    }

    fun release() {
        if (--counter <= 0) {
            free(this)
        }
    }
}