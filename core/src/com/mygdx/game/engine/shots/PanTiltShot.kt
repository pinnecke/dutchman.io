package com.mygdx.game.engine.shots

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.utils.Deferred

class PanTiltShot(
    factory: Deferred<ShotFactory>,
    beginLeft: Float, beginBottom: Float, beginDimension: Float, beginType: ShotDimension,
    endLeft: Float, endBottom: Float, endDimension: Float, endType: ShotDimension,
    duration: Float,
    val xDuration: Float = duration,
    val yDuration: Float = duration,
    val sDuration: Float = duration,
    val interpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
    caption: String = "Pan Shot",
    onStart: () -> Unit = {},
    onDone: () -> Unit = {},
    onUpdates: (dt: Float, elapsed: Float, progress: Float) -> Unit = { _, _, _ -> },
    var debuggable: Boolean = true
): Shot(factory, caption, duration, onStart, onDone, onUpdates) {

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

    init {
        managedContent.add(begin)
        managedContent.add(end)
    }


    private var xTween: TweenProcessor? = null
    private var yTween: TweenProcessor? = null
    private var sTween: TweenProcessor? = null

    /*override fun onCut() {
        with (camera!!) {
            position.x = begin.center.x
            position.y = begin.center.y
            zoom = begin.zoom
        }

        with (camera!!) {
            xTween = Tween(
                duration = xDuration,
                origin = { position.x },
                target = { end.center.x },
                onUpdate = { position.x = it },
                interpolate = interpolation
            )
            xTween!!.start()

            yTween = Tween(
                duration = yDuration,
                origin = { position.y },
                target = { end.center.y },
                onUpdate = { position.y = it },
                interpolate = interpolation
            )
            yTween!!.start()

            sTween = Tween(
                duration = sDuration,
                origin = { zoom },
                target = { end.zoom },
                onUpdate = { zoom = it },
                interpolate = interpolation
            )
            sTween!!.start()
        }
    }

    override fun onUpdate(dt: Float) {
        begin.update(dt)
        end.update(dt)
        xTween?.update(dt)
        yTween?.update(dt)
        sTween?.update(dt)
    }*/

    override fun render(batch: SpriteBatch) {
        if (debuggable) {
            begin.render(batch)
            end.render(batch)
        }
    }
}