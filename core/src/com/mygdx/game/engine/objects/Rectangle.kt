package com.mygdx.game.engine.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.game.engine.stdx.GameObject

class Rectangle(
    private val x: Float,
    private val y: Float,
    private val width: Float,
    private val height: Float,
    private val color: Color
): GameObject {

    private var shapeRenderer: ShapeRenderer? = null
    private var destroyed = false

    override fun create() {
        shapeRenderer = ShapeRenderer()
        shapeRenderer!!.color = this.color
    }

    override fun render(batch: SpriteBatch) {
        if (!destroyed) {
            batch.end()
            shapeRenderer!!.projectionMatrix = batch.projectionMatrix
            shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderer!!.rect(x, y, width, height)
            shapeRenderer!!.end()
            batch.begin()
        }
    }

    override fun destroy() {
        shapeRenderer!!.dispose()
        destroyed = true
    }
}