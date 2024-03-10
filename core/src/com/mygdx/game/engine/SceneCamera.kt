package com.mygdx.game.engine

import com.badlogic.gdx.graphics.OrthographicCamera
import com.mygdx.game.engine.stdx.Update

data class CutEffectDescriptor(
    val duration: Float,
    val xDuration: Float = duration,
    val yDuration: Float = duration,
    val zoomDuration: Float = duration,
    val rotationDuration: Float = duration,
    val xInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
    val yInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
    val zoomInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
    val rotationInterpolation: Interpolation = TweenFunction.EASE_IN_OUT.fn,
) {
    companion object {
        fun smooth(duration: Float) = CutEffectDescriptor(
            duration = duration
        )
    }
}

class SceneCamera(
): Update {
    var camera: OrthographicCamera? = null

    private var x: Float = 0f
    private var y: Float = 0f
    private var zoom: Float = 1f
    private var rotation: Float = 0f

    private var tweenX: Tween? = null
    private var tweenY: Tween? = null
    private var tweenZoom: Tween? = null
    private var tweenRotation: Tween? = null

    private var tweenXDone = false
    private var tweenYDone = false
    private var tweenZoomDone = false
    private var tweenRotationDone = false

    private val allDone: Boolean
        get() = tweenXDone && tweenYDone && tweenZoomDone && tweenRotationDone

    override fun update(dt: Float) {
        if (!allDone) {
            tweenX?.update(dt)
            tweenY?.update(dt)
            tweenZoom?.update(dt)
            tweenRotation?.update(dt)
        }
    }

    fun hardCut(
        panel: Panel
    ) {
        tweenXDone = true
        tweenYDone = true
        tweenZoomDone = true
        tweenRotationDone = true

        camera?.position?.x = panel.center.x
        camera?.position?.y = panel.center.y
        camera?.zoom = panel.zoom
        camera?.rotate(panel.rotation)
    }

    fun cut(
        panel: Panel,
        effect: CutEffectDescriptor,
        onDone: () -> Unit = {}
    ) {
        tweenX = Tween(
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
        tweenY = Tween(
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
        tweenZoom = Tween(
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
        tweenRotation = Tween(
            duration = effect.rotationDuration,
            origin = { rotation },
            target = { panel.rotation },
            onStart = { tweenRotationDone = false },
            onUpdate = {
                rotation = it
                camera!!.rotate(rotation)
            },
            onDone = {
                tweenRotationDone = true
                if (allDone) {
                    onDone()
                }
            },
            interpolate = effect.rotationInterpolation
        )
        tweenX?.start()
        tweenY?.start()
        tweenZoom?.start()
        tweenRotation?.start()
    }

}