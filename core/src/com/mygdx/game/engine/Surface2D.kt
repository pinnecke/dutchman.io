package com.mygdx.game.engine

import com.badlogic.gdx.math.Vector2

data class Surface2D (
    var left: Float,
    var bottom: Float,
    var width: Float,
    var height: Float
) {
    val center = Vector2(left + width / 2f, bottom + height / 2f)
    val top = bottom + height
    val right = left + width
}

data class CanvasDimension (
    val surface: Surface2D,
    val safeZone: Surface2D
)