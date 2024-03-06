package com.mygdx.game.engine.utils

fun <T> deferred(resolve: () -> T) = Deferred(resolve)

class Deferred<T> internal constructor(private val resolve: () -> T) {

    private var resolved: T? = null

    fun <R> map(action: (inner: T) -> R): Deferred<R> = deferred { action(unwrap()) }

    fun unwrap(): T {
        if (resolved == null) {
            resolved = resolve()
        }
        return resolved!!
    }
}