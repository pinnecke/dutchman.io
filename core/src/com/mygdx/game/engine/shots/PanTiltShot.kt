package com.mygdx.game.engine.shots

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.utils.Deferred

class PanShot(
    factory: Deferred<ShotFactory>,
    beginLeft: Float, beginBottom: Float, beginDimension: Float, beginType: ShotDimension,
    endLeft: Float, endBottom: Float, endDimension: Float, endType: ShotDimension,
    val xTranslationDuration: Float,
    val yTranslationDuration: Float = xTranslationDuration,
    val sTranslationDuration: Float = xTranslationDuration,
    val interpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
    caption: String = "Pan Shot",
    var debuggable: Boolean = true
): Shot(factory) {

    private val begin = Panel(
        surface = Surface2D(
            left = beginLeft,
            bottom = beginBottom,
            width = if (beginType == ShotDimension.WIDTH) {
                beginDimension
            } else {
                (beginDimension / Engine.canvas.surface.height) * Engine.canvas.surface.width
            },
            height = if (beginType == ShotDimension.HEIGHT) {
                beginDimension
            } else {
                (beginDimension / Engine.canvas.surface.width) * Engine.canvas.surface.height
            }
        ),
        caption = "$caption: ${this::class.simpleName} (Begin)"
    )

    private val end = Panel(
        surface = Surface2D(
            left = endLeft,
            bottom = endBottom,
            width = if (endType == ShotDimension.WIDTH) {
                endDimension
            } else {
                (endDimension / Engine.canvas.surface.height) * Engine.canvas.surface.width
            },
            height = if (endType == ShotDimension.HEIGHT) {
                endDimension
            } else {
                (endDimension / Engine.canvas.surface.width) * Engine.canvas.surface.height
            }
        ),
        caption = "$caption: ${this::class.simpleName} (End)"
    )

    private var xTween: Tween? = null
    private var yTween: Tween? = null
    private var sTween: Tween? = null

    override fun setup() {
        begin.create()
        end.create()
    }

    override fun destroy() {
        begin.destroy()
        end.destroy()
    }

    override fun cut(onDone: () -> Unit) {
        with (camera!!) {
            position.x = begin.center.x
            position.y = begin.center.y
            zoom = begin.zoom
        }
        onDone()
    }

    override fun apply(onDone: () -> Unit) {
        with (camera!!) {
            xTween = Tween(
                duration = xTranslationDuration,
                origin = { position.x },
                target = { end.center.x },
                onUpdate = { position.x = it },
                interpolate = interpolation
            )
            xTween!!.start()
        }
    }

    override fun update(dt: Float) {
        begin.update(dt)
        end.update(dt)
        xTween?.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        if (debuggable) {
            begin.render(batch)
            end.render(batch)
        }
    }
}