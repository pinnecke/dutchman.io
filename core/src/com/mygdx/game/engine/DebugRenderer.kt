package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4

private val IDENTITY = Matrix4()

class DebugRenderer(
    val enabled: Boolean,
    var color: Color = Color.RED
) {
    private val shapeRenderer = ShapeRenderer()

    fun drawLine(projectionMatrix: Matrix4?, x0: Float, y0: Float, x1: Float, y1: Float, lineWidth: Int = 1) {
        if (enabled) {
            Gdx.gl.glLineWidth(lineWidth.toFloat())
            if (projectionMatrix != null) {
                shapeRenderer.projectionMatrix = projectionMatrix
            } else {
                shapeRenderer.projectionMatrix = IDENTITY
            }
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = color
            shapeRenderer.line(x0, y0, x1, y1)
            shapeRenderer.end()
            Gdx.gl.glLineWidth(1f)
        }
    }

    fun drawRect(
        projectionMatrix: Matrix4?,
        x: Float, y: Float,
        width: Float, height: Float,
        lineWidth: Int = 1
    ) = drawRect(
            projectionMatrix,
            x, y,
            x, height,
            width, height,
            width, y,
            lineWidth
        )

    fun drawRect(
        projectionMatrix: Matrix4?,
        x0: Float, y0: Float,
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        x3: Float, y3: Float,
        lineWidth: Int = 1
    ) {
        drawLine(projectionMatrix, x0, y0, x1, y1, lineWidth)
        drawLine(projectionMatrix, x1, y1, x2, y2, lineWidth)
        drawLine(projectionMatrix, x2, y2, x3, y3, lineWidth)
        drawLine(projectionMatrix, x3, y3, x0, y0, lineWidth)
    }
}