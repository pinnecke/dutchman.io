package com.mygdx.game.engine

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.stdx.Update
import com.mygdx.game.engine.utils.Deferred

class ShotFactory(
    private val sceneManager: Deferred<SceneManager>
): Update {
    val camera: OrthographicCamera
        get() { return sceneManager.unwrap().worldCamera!! }
}

abstract class Shot(private var factory: Deferred<ShotFactory>): GameObject {
    var camera: OrthographicCamera? = null

    abstract fun setup()
    abstract fun cut(onDone: () -> Unit = { })
    abstract fun apply(onDone: () -> Unit = { })

    override fun create() {
        camera = factory.unwrap().camera
        setup()
    }

}

enum class ShotDimension {
    WIDTH,
    HEIGHT
}

class StaticShot(
    factory: Deferred<ShotFactory>,
    left: Float, bottom: Float, dimension: Float, type: ShotDimension,
    caption: String = "Static Shot",
    private val debuggable: Boolean = true
): Shot(factory) {

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
        caption = caption
    )

    override fun setup() {
        panel.create()
    }

    override fun destroy() {
        panel.destroy()
    }

    override fun cut(onDone: () -> Unit) {
        with (camera!!) {
            position.x = panel.center.x
            position.y = panel.center.y
            zoom = panel.zoom
        }
        onDone()
    }

    override fun apply(onDone: () -> Unit) = cut(onDone)

    override fun update(dt: Float) {
        panel.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        if (debuggable) {
            panel.render(batch)
        }
    }
}


class SceneTransition: GameObject {
    private var x: Float = 0f
    private var y: Float = 0f
    private var zoom: Float = 1f

    var camera: OrthographicCamera? = null

    private var tweenX: Tween? = null
    private var tweenY: Tween? = null
    private var tweenZoom: Tween? = null

    private var tweenXDone = false
    private var tweenYDone = false
    private var tweenZoomDone = false

    override fun create() {

    }

    override fun destroy() {

    }

    fun cut(
        target: Panel,
        onDone: () -> Unit = {}
    ) = with (camera!!) {
        with(position) {
            x = target.center.x
            y = target.center.y
        }
        zoom = target.zoom
        onDone()
    }

    fun move(
        shot: Panel,
        xDuration: Float,
        yDuration: Float = xDuration,
        zoomDuration: Float = yDuration,
        xInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
        yInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
        zoomInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
        onDone: () -> Unit = {}
    ) {
        tweenX = Tween(
            duration = xDuration,
            origin = { camera!!.position.x },
            target = { shot.center.x },
            onInit = { x = camera!!.position.x },
            onUpdate = {
                x = it
                camera!!.position.x = it
            },
            onStart = {
                tweenXDone = false
                tweenYDone = false
                tweenZoomDone = false
            },
            onDone = {
                tweenXDone = true
                if (tweenYDone && tweenZoomDone) {
                    onDone()
                }
            },
            interpolate = xInterpolation
        )
        tweenY = Tween(
            duration = yDuration,
            origin = { camera!!.position.y },
            target = { shot.center.y },
            onInit = { y = camera!!.position.y },
            onUpdate = {
                y = it
                camera!!.position.y = it
            },
            onDone = {
                tweenYDone = true
                if (tweenXDone && tweenZoomDone) {
                    onDone()
                }
            },
            interpolate = yInterpolation
        )
        tweenZoom = Tween(
            duration = zoomDuration,
            origin = { camera!!.zoom },
            target = { shot.zoom },
            onInit = { zoom = camera!!.zoom },
            onUpdate = {
                zoom = it
                camera!!.zoom = it
            },
            onDone = {
                tweenZoomDone = true
                if (tweenXDone && tweenYDone) {
                    onDone()
                }
            },
            interpolate = zoomInterpolation
        )
        tweenX?.start()
        tweenY?.start()
        tweenZoom?.start()
    }

    override fun update(dt: Float) {
        tweenX?.update(dt)
        tweenY?.update(dt)
        tweenZoom?.update(dt)
    }

}

