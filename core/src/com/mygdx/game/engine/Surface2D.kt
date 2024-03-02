package com.mygdx.game.engine

data class Surface2D (
    val left: Float,
    val bottom: Float,
    val width: Float,
    val height: Float
)

data class CanvasDimension (
    val surface: Surface2D,
    val safeZone: Surface2D
)