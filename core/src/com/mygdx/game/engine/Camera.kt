package com.mygdx.game.engine

import com.badlogic.gdx.graphics.OrthographicCamera
import com.mygdx.game.DiagnosticsPanel
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.stdx.Update
import com.mygdx.game.engine.utils.Deferred

class ShotFactory(
    private val sceneManager: Deferred<SceneManager>
): Update {
    val worldCamera: OrthographicCamera
        get() { return sceneManager.unwrap().worldCamera!! }
    val diagnosticsPanel: DiagnosticsPanel
        get() { return sceneManager.unwrap().diagnostics!! }
}

abstract class Shot(
    private var factory: Deferred<ShotFactory>,
    private var name: String = "Untitled",
    duration: Float,
    onStart: () -> Unit,
    onDone: () -> Unit,
    onUpdates: (dt: Float, elapsed: Float, progress: Float) -> Unit
): GameObject("Shot - $name") {
    var camera: OrthographicCamera? = null
    var diagnostics: DiagnosticsPanel? = null

    private val sequencer = Sequencer(
        duration = duration,
        onStart = onStart,
        onDone = onDone,
        onUpdate = { dt, elapsed, progress ->
            /*with(diagnostics!!) {
                shotElapsed = progress * duration
                shotTotal = duration
                shotProgress = progress
            }*/
            onUpdates(dt, elapsed, progress)
        }
    )

    override val managedContent: MutableList<ManagedContent> = mutableListOf(
        managedContentOf(
            id = "Wiring members",
            load = {
                with (factory.unwrap()) {
                    camera = worldCamera
                    diagnostics = diagnosticsPanel
                }
            },
            unload = {

            }
        )
    )

    final override fun update(dt: Float) {
        sequencer.update(dt)
    }

    fun cut() {
        sequencer.start()

/*        with(diagnostics!!) {
            sceneName = scene.name
            shotName = name
        }*/
    }

}

enum class ShotDimension {
    WIDTH,
    HEIGHT
}


class SceneTransition: GameObject("Scene Transition Effect") {

    override val managedContent: MutableList<ManagedContent> = mutableListOf()

    private var x: Float = 0f
    private var y: Float = 0f
    private var zoom: Float = 1f

    var camera: OrthographicCamera? = null

    private var tweenX: TweenProcessor? = null
    private var tweenY: TweenProcessor? = null
    private var tweenZoom: TweenProcessor? = null

    private var tweenXDone = false
    private var tweenYDone = false
    private var tweenZoomDone = false

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
        tweenX = TweenProcessor(
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
        tweenY = TweenProcessor(
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
        tweenZoom = TweenProcessor(
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

