package com.mygdx.game.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.game.engine.stdx.Render
import kotlin.math.max
import kotlin.math.min

typealias CoordinateProjector = (x: Float, y: Float) -> Vector2

class SpeechBubblePivot(
    var x: Float,
    var y: Float,
    val screenToOverlay: CoordinateProjector
): Render {
    private val debugRenderer = DebugRenderer(Config.DEBUG_RENDER_SHOW_PIVOTS_POINTS)
    private val hPadding = 460f
    private val vPadding = 120f

    var boxedX: Float = x
        get() {
            val overlay = screenToOverlay(x, y)
            return max(hPadding, min(overlay.x, Config.WINDOW_WIDTH.toFloat() - hPadding))
        }

    var boxedY: Float = y
        get() {
            val overlay = screenToOverlay(x, y)
            return max(vPadding + 120f, min(overlay.y, Config.WINDOW_HEIGHT.toFloat() - vPadding))
        }



    override fun render(batch: SpriteBatch) {
        batch.end()
        debugRenderer.drawLine(
            batch.projectionMatrix,
            x - 15, y - 15, x + 15, y + 15,
            4
        )
        debugRenderer.drawLine(
            batch.projectionMatrix,
            x - 15, y + 15, x + 15, y - 15,
        4
        )
        batch.begin()
    }
}