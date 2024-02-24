package com.mygdx.game.engine.stdx

fun once() = Auto(isAuto = false, isInfinite = false, value = 1)
fun <T> infinite() = Auto<T>(isAuto = false, isInfinite = true, value = null)
fun <T> value(value: T) = Auto(isAuto = false, isInfinite = false, value = value)
fun <T> auto() = Auto<T>(isAuto = true, isInfinite = false, value = null)

class Auto<T> internal constructor(
    private val isAuto: Boolean,
    val isInfinite: Boolean,
    private val value: T?
) {
    fun get(auto: () -> T): T = if (isAuto) {
        auto()
    } else {
        value!!
    }
}