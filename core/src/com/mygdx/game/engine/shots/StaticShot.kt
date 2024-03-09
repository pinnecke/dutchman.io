package com.mygdx.game.engine.shots

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.utils.Deferred

class StaticShot(
    factory: Deferred<ShotFactory>,
    duration: Float,
    left: Float, bottom: Float, dimension: Float, type: ShotDimension,
    caption: String = "Static Shot",
    onStart: () -> Unit = {},
    onDone: () -> Unit = {},
    onUpdates: (dt: Float, elapsed: Float, progress: Float) -> Unit = {_, _, _ -> },
    var debuggable: Boolean = true
): Shot(factory, caption, duration, onStart, onDone, onUpdates) {
    val initialDebuggable = debuggable

    private val panel = Panel(
        surface = Surface2D(
            left = left,
            bottom = bottom,
            width = if (type == ShotDimension.WIDTH) {
                dimension
            } else {
                (dimension / Engine.canvas.surface.height) * Engine.canvas.surface.width
            },
            height = if (type == ShotDimension.HEIGHT) {
                dimension
            } else {
                (dimension / Engine.canvas.surface.width) * Engine.canvas.surface.height
            }
        ),
        caption = "$caption"
    )

    init {
        managedContent.add(panel)
    }

/*
    override fun onCut() {
      /*  with (camera!!) {
            position.x = panel.center.x
            position.y = panel.center.y
            zoom = panel.zoom
        }*/
    }

    override fun onUpdate(dt: Float) {
        panel.update(dt)
    }*/

    override fun render(batch: SpriteBatch) {
        if (debuggable) {
            panel.render(batch)
        }
    }
}