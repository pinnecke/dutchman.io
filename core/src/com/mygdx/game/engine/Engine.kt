package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color

data class Colors(
    val dimmedRed: Color = Color(1.0f, 0f, 0f, 0.60f),
    val dimmedGray: Color = Color(0f, 0f, 0f, 0.60f),
    val lightGray: Color = Color(0f, 0f, 0f, 0.10f),
    val darkDimmedGray: Color = Color(0f, 0f, 0f, 0.85f)
)

private fun Int.fraq(): Float = this/255f
private fun color(r: Int, g: Int, b: Int, a: Int = 255) = Color(r.fraq(), g.fraq(), b.fraq(), a.fraq())

enum class TextColor(
    val color: Color
) {
    BLUE(color(86, 87, 255)),
    DARK_ORANGE(color(247, 136, 2)),
    GREEN(color(51, 212, 33)),
    LIGHT_GREEN(color(87, 253, 88)),
    LIGHT_ORANGE(color(252, 196, 132)),
    PETROL(color(4, 189, 157)),
    PURPLE(color(253, 77, 255)),
    RED(color(250, 87, 90)),
    WHITE(color(255, 255, 255)),
    YELLOW(color(255, 255, 82))
}

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