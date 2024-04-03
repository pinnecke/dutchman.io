package com.mygdx.game.engine.stdx

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.pow

fun ShapeRenderer.roundedRect(
    r: Float, g: Float, b: Float, a: Float,
    x: Float, y: Float, width: Float, height: Float, radius: Float,
    samples: Int = 7,
) {
    end()
    begin(ShapeRenderer.ShapeType.Filled)

    var c = a / samples * 2

    for (i in 1 .. samples) {
        setColor(r, g, b, a - (c * (i/samples.toFloat())))

        // Central rectangle
        rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius)

        // Four side rectangles, in clockwise order
        rect(x + radius, y, width - 2 * radius, radius)
        rect(x + width - radius, y + radius, radius, height - 2 * radius)
        rect(x + radius, y + height - radius, width - 2 * radius, radius)
        rect(x, y + radius, radius, height - 2 * radius)

        // Four arches, clockwise too
        val segments = 2.0.pow(i).toInt()
        arc(x + radius, y + radius, radius, 180f, 90f, segments)
        arc(x + width - radius, y + radius, radius, 270f, 90f, segments)
        arc(x + width - radius, y + height - radius, radius, 0f, 90f, segments)
        arc(x + radius, y + height - radius, radius, 90f, 90f, segments)
    }

    end()
    begin(ShapeRenderer.ShapeType.Filled)
}