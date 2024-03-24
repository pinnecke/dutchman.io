package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject

enum class CinematicBarState {
    BARS_IN,
    BARS_PRESENT,
    BARS_ABSENT,
    BARS_OUT
}

class CinematicBars: GameObject("Cinematic Bards") {

    override val managedContent = mutableListOf(
        managedContentOf(
            id = "Camera Setup",
            load = {
                viewport = StretchViewport(Config.WINDOW_WIDTH.toFloat(), Config.WINDOW_HEIGHT.toFloat())
                viewport!!.apply()
            },
            unload = { }
        ),
        managedContentOf(
            id = "Shape Renderer",
            load = {
                shapeRenderer = ShapeRenderer()
                shapeRenderer!!.color = Color.BLACK
            },
            unload = {
                shapeRenderer!!.dispose()
            }
        ),
        managedContentOf(
            id = "State setup",
            load = {
                destroyed = false
            },
            unload = {
                destroyed = true
            }
        )
    )

    private val barWidth = Config.WINDOW_WIDTH.toFloat()
    private val maximumBarHeight = 0.1f * Config.WINDOW_HEIGHT.toFloat()

    private var viewport: Viewport? = null
    private var shapeRenderer: ShapeRenderer? = null

    private var destroyed = false

    private var state = CinematicBarState.BARS_ABSENT
    private var actualBarHeight = 0f
    private var tween = TweenProcessor(
        duration = 0.5f,
        interpolate = TweenFunction.EASE_IN_OUT.fn,
        origin = { 0f },
        target = { maximumBarHeight },
        onInit = { actualBarHeight = 0f },
        onUpdate = { actualBarHeight = it },
        onDone = {
            if (state == CinematicBarState.BARS_IN) {
                state = CinematicBarState.BARS_PRESENT
            } else if (state == CinematicBarState.BARS_OUT) {
                state = CinematicBarState.BARS_ABSENT
            }
        }
    )

    fun show() {
        if (state == CinematicBarState.BARS_ABSENT) {
            state = CinematicBarState.BARS_IN
            tween.start()
        }
    }

    fun hide() {
        if (state == CinematicBarState.BARS_PRESENT) {
            state = CinematicBarState.BARS_OUT
            tween.start()
        }
    }

    fun isPresent() = state == CinematicBarState.BARS_PRESENT || state == CinematicBarState.BARS_IN
    fun isAbsent() = state == CinematicBarState.BARS_ABSENT || state == CinematicBarState.BARS_OUT


    override fun update(dt: Float) {
        viewport!!.update(Gdx.graphics.width, Gdx.graphics.height, true)
        viewport!!.apply()
        tween.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        if (!destroyed) {
            shapeRenderer!!.projectionMatrix = viewport!!.camera.combined
            shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)

            if (state == CinematicBarState.BARS_IN || state == CinematicBarState.BARS_PRESENT) {
                shapeRenderer!!.rect(0f, Config.WINDOW_HEIGHT.toFloat() - actualBarHeight + 1f, barWidth, maximumBarHeight)
                shapeRenderer!!.rect(0f, 0f, barWidth, actualBarHeight)
            } else if (state == CinematicBarState.BARS_OUT) {
                shapeRenderer!!.rect(0f, Config.WINDOW_HEIGHT.toFloat() - maximumBarHeight + 1f + actualBarHeight, barWidth, maximumBarHeight)
                shapeRenderer!!.rect(0f, 0f, barWidth, maximumBarHeight - actualBarHeight)
            }

            shapeRenderer!!.end()
        }
    }

}