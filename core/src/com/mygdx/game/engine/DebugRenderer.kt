package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.mygdx.game.engine.memory.AllocatorManagedContent
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.objects.Label

private val IDENTITY = Matrix4()

class DebugRenderer(
    renderContextName: String,
    private val enabled: Boolean,
    var renderColor: Color = Color.RED
): AllocatorManagedContent("Debug Renderer ($renderContextName)") {

    private var shapeRenderer: ShapeRenderer? = null
    private var font = Label(
        fontSize = 16,
        textColor = Color.WHITE,
        borderColor = Engine.colors.darkDimmedGray,
        borderWidth = 2f
    )

    override val managedContent = mutableListOf(
        managedContentOf(
            contentIdentifier = "Shape Renderer",
            load = {
                shapeRenderer = ShapeRenderer()
            },
            unload = {
                shapeRenderer!!.dispose()
            }
        ),
        font
    )

    fun print(batch: SpriteBatch, text: String, x: Float, y: Float) {
        if (enabled) {
            font.text = text
            font.x = x
            font.y = y
            font.render(batch)
        }
    }

    fun render(batch: SpriteBatch, action: (renderer: DebugRenderer) -> Unit) {
        if (enabled) {
            batch.end()
            action(this)
            batch.begin()
        }
    }

    fun line(targetProjectionMatrix: Matrix4?, x0: Float, y0: Float, x1: Float, y1: Float, lineWidth: Int = 1) {
        if (enabled) {
            with (shapeRenderer!!) {
                Gdx.gl.glLineWidth(lineWidth.toFloat())
                if (projectionMatrix != null) {
                    projectionMatrix = targetProjectionMatrix
                } else {
                    projectionMatrix = IDENTITY
                }
                begin(ShapeRenderer.ShapeType.Line)
                color = renderColor
                line(x0, y0, x1, y1)
                end()
                Gdx.gl.glLineWidth(1f)
            }
        }
    }

    fun rect(
        targetProjectionMatrix: Matrix4?,
        x: Float, y: Float,
        width: Float, height: Float,
        lineWidth: Int = 1
    ) {
        if (enabled) {
            with (shapeRenderer!!) {
                Gdx.gl.glLineWidth(lineWidth.toFloat())
                projectionMatrix = if (projectionMatrix != null) {
                    targetProjectionMatrix
                } else {
                    IDENTITY
                }
                begin(ShapeRenderer.ShapeType.Line)
                color = renderColor
                rect(x, y, width, height)
                end()
                Gdx.gl.glLineWidth(1f)
            }
        }
    }

    fun filled(
        targetProjectionMatrix: Matrix4?,
        x: Float, y: Float,
        width: Float, height: Float,
        lineWidth: Int = 1
    ) {
        if (enabled) {
            with (shapeRenderer!!) {
                Gdx.gl.glEnable(GL20.GL_BLEND)
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                Gdx.gl.glLineWidth(lineWidth.toFloat())

                projectionMatrix = if (projectionMatrix != null) {
                    targetProjectionMatrix
                } else {
                    IDENTITY
                }
                begin(ShapeRenderer.ShapeType.Filled)
                color = renderColor
                rect(x, y, width, height)
                end()
                Gdx.gl.glLineWidth(1f)
            }
        }
    }
}