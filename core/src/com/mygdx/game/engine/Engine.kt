package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color

data class Colors(
    val dimmedRed: Color = Color(1.0f, 0f, 0f, 0.60f),
    val dimmedGray: Color = Color(0f, 0f, 0f, 0.60f),
    val lightGray: Color = Color(0f, 0f, 0f, 0.10f),
    val darkDimmedGray: Color = Color(0f, 0f, 0f, 0.85f)
)

class Engine {

    companion object {
        val canvas = CanvasDimension(
            surface = Surface2D(
                0f, 0f, 1920f, 1200f
            ),
            safeZone = Surface2D(
                160f, 75f, 1760f, 1125f
            )
        )
        val colors = Colors()
    }

}