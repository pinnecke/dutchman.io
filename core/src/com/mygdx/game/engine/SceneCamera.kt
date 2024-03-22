package com.mygdx.game.engine

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.mygdx.game.engine.stdx.Update

data class CutEffectDescriptor(
    val duration: Float,
    val xDuration: Float = duration,
    val yDuration: Float = duration,
    val zoomDuration: Float = duration,
    val interpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
    val xInterpolation: Interpolation = interpolation,
    val yInterpolation: Interpolation = interpolation,
    val zoomInterpolation: Interpolation = interpolation
) {
    companion object {
        fun cut() = CutEffectDescriptor(
            duration = 0f,
            interpolation = TweenFunction.LINEAR.fn
        )
        fun smooth(duration: Float) = CutEffectDescriptor(
            duration = duration,
            interpolation = TweenFunction.EASE_IN_OUT.fn
        )
        fun easeIn(duration: Float) = CutEffectDescriptor(
            duration = duration,
            interpolation = TweenFunction.EASE_IN.fn
        )
    }
}

private val rotationAxis = Vector3(0f, 0f, 1f)

class SceneCamera: Update {
    var camera: OrthographicCamera? = null

    private var x: Float = 0f
    private var y: Float = 0f
    private var zoom: Float = 1f

    private var tweenX: TweenProcessor? = null
    private var tweenY: TweenProcessor? = null
    private var tweenZoom: TweenProcessor? = null

    private var tweenXDone = false
    private var tweenYDone = false
    private var tweenZoomDone = false

    private val allDone: Boolean
        get() = tweenXDone && tweenYDone && tweenZoomDone

    override fun update(dt: Float) {
        if (!allDone) {
            tweenX?.update(dt)
            tweenY?.update(dt)
            tweenZoom?.update(dt)
        }
    }

    fun hardCut(
        panel: Panel
    ) {
        tweenXDone = true
        tweenYDone = true
        tweenZoomDone = true

        camera?.position?.x = panel.center.x
        camera?.position?.y = panel.center.y
        camera?.zoom = panel.zoom
    }

    fun cut(
        panel: Panel,
        effect: CutEffectDescriptor,
        onDone: () -> Unit = {}
    ) {
        tweenX = TweenProcessor(
            duration = effect.xDuration,
            origin = { camera!!.position.x },
            target = { panel.center.x },
            onInit = { x = camera!!.position.x },
            onUpdate = {
                x = it
                camera!!.position.x = it
            },
            onStart = { tweenXDone = false },
            onDone = {
                tweenXDone = true
                if (allDone) {
                    onDone()
                }
            },
            interpolate = effect.xInterpolation
        )
        tweenY = TweenProcessor(
            duration = effect.yDuration,
            origin = { camera!!.position.y },
            target = { panel.center.y },
            onInit = { y = camera!!.position.y },
            onStart = { tweenYDone = false },
            onUpdate = {
                y = it
                camera!!.position.y = it
            },
            onDone = {
                tweenYDone = true
                if (allDone) {
                    onDone()
                }
            },
            interpolate = effect.yInterpolation
        )
        tweenZoom = TweenProcessor(
            duration = effect.zoomDuration,
            origin = { camera!!.zoom },
            target = { panel.zoom },
            onInit = { zoom = camera!!.zoom },
            onStart = { tweenZoomDone = false },
            onUpdate = {
                zoom = it
                camera!!.zoom = it
            },
            onDone = {
                tweenZoomDone = true
                if (allDone) {
                    onDone()
                }
            },
            interpolate = effect.zoomInterpolation
        )
        tweenX?.start()
        tweenY?.start()
        tweenZoom?.start()
    }

}