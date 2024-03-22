package com.mygdx.game.engine.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject

class Rectangle(
    val x: Float,
    val y: Float,
    var width: Float,
    val height: Float,
    private val color: Color
): GameObject("Rectangle - ($x, $y, $width, $height)") {

    private var shapeRenderer: ShapeRenderer? = null

    override val managedContent = mutableListOf(
        managedContentOf(
            id = "Shape Renderer",
            load = {
                shapeRenderer = ShapeRenderer()
            },
            unload = {
                shapeRenderer!!.dispose()
            }
        )
    )

    override fun render(batch: SpriteBatch) {
        batch.end()

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRenderer!!.projectionMatrix = batch.projectionMatrix


        shapeRenderer!!.color = this.color
        shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer!!.rect(x, y, width, height)
        shapeRenderer!!.end()

        batch.begin()
    }
}