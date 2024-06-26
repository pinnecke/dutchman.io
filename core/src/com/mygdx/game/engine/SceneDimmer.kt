package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject

class SceneDimmer: GameObject("Scene Dimming Effect") {

    private var shapeRenderer: ShapeRenderer? = null

    override val managedContent = mutableListOf(
        managedContentOf(
            "Shape Renderer",
            load = {
                shapeRenderer = ShapeRenderer()
                shapeRenderer!!.color = Color.BLACK
            },
            unload = {
                shapeRenderer!!.dispose()
                destroyed = true
            }
        )
    )

    enum class DimSpeed(val duration: Float) {
        SLOW(0.5f),
        MEDIUM(0.2f),
        HIGH(0.05f)
    }


    private var destroyed = false

    private var alpha: Float = 0.0f
    private var tween: TweenProcessor? = null

    fun apply(targetAmount: Float, speed: DimSpeed, onDone: () -> Unit) {
        if (tween == null) {
            tween = TweenProcessor(
                duration = speed.duration,
                origin = { alpha },
                target = { targetAmount },
                onUpdate = {
                    alpha = it
                },
                onDone = {
                    onDone
                    tween = null
                },
                interpolate = TweenFunction.EASE_IN_OUT.fn
            )
            tween!!.start()
        }
    }

    override fun update(dt: Float) {
        if (tween != null) {
            tween!!.update(dt)
        }
    }

    override fun render(batch: SpriteBatch) {
        if (!destroyed) {
            with (shapeRenderer!!) {
                batch.end()
                projectionMatrix = batch.projectionMatrix

                Gdx.gl.glEnable(GL20.GL_BLEND)
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

                begin(ShapeRenderer.ShapeType.Filled)
                setColor(color.r, color.g, color.b, alpha)

                rect(
                    0f, 0f,
                    Engine.canvas.surface.width, Engine.canvas.surface.height
                )

                end()
                batch.begin()
            }
        }
    }
}