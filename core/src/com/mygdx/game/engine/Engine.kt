package com.mygdx.game.engine

class Engine {

    companion object {
        val canvas: CanvasDimension = CanvasDimension(
            surface = Surface2D(
                0f, 0f, 1920f, 1200f
            ),
            safeZone = Surface2D(
                160f, 75f, 1600f, 1050f
            )
        )
    }

}