package com.mygdx.game.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.DiagnosticsPanel
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.DynamicComponent
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.stdx.Render
import com.mygdx.game.engine.stdx.Update
import com.mygdx.game.engine.utils.info
import kotlin.math.max
import kotlin.math.min

interface TimeFrame {

    val duration: Float
    var elapsed: Float
    val alpha: Float
        get() = max(0f, min(elapsed/duration, 1f))

}

interface Player {
    fun start()
    fun pause()
    fun resume()
    fun stop()
    fun rewind()
}

interface Playback: Player {
    val isRunning: Boolean
    val isPaused: Boolean
    val hasEnded: Boolean
    val hasStopped: Boolean
}

abstract class Sequence: Update, Render, TimeFrame, Playback {

    override var elapsed = 0f

    override var isRunning = false
    override var isPaused = false
    override var hasEnded = false
    override var hasStopped = false
    
    abstract fun onStart()
    abstract fun onReset()
    abstract fun onDone()
    abstract fun onUpdate(dt: Float, alpha: Float, elapsed: Float, total: Float)
    abstract fun onRender(batch: SpriteBatch)

    override fun start() {
        if (isRunning) {
            stop()
            rewind()
        }
        isRunning = true
        onStart()
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun stop() {
        hasStopped = true
        isRunning = false
    }

    override fun rewind() {
        elapsed = 0f
        isRunning = false
        isPaused = false
        hasEnded = false
        hasStopped = false
        onReset()
    }

    override fun update(dt: Float) {
        if (isRunning && !isPaused) {
            elapsed += dt
            onUpdate(dt, alpha, elapsed, duration)
            if (elapsed > duration) {
                hasEnded = true
                isRunning = false
                elapsed = 0f
                onDone()
            }
        }
    }

    override fun render(batch: SpriteBatch) {
        onRender(batch)
    }
}

class Lane(
    private val name: String,
    private val sequences: List<Sequence>,
    private val onStart: Action = noAction,
    private val onUpdate: Updater = noUpdates,
    private val onDone: Action = noAction
): GameObject("Game Scene Lane - $name"), Playback, TimeFrame {

    override val managedContent = mutableListOf<ManagedContent>()

    override val duration: Float
        get() { return sequences.maxOf { it.duration } }

    override var elapsed = 0f

    override val isRunning: Boolean
        get() = sequences.any { it.isRunning }

    override val isPaused: Boolean
        get() = sequences.all { it.isPaused }

    override val hasEnded: Boolean
        get() = sequences.all { it.hasEnded }

    override val hasStopped
        get() = sequences.all { it.hasStopped }

    override fun update(dt: Float) {
        if (isRunning) {
            elapsed += dt
            sequences.forEach { it.update(dt) }
            onUpdate(dt, alpha, elapsed, duration)
            if (!isRunning) {
                onDone()
            }
        }
    }

    override fun render(batch: SpriteBatch) {
        sequences.forEach { it.render(batch) }
    }

    override fun start() {
        elapsed = 0f
        sequences.forEach { it.start() }
        onStart()
    }

    override fun pause() {
        sequences.forEach { it.pause() }
    }

    override fun resume() {
        sequences.forEach { it.resume() }
    }

    override fun stop() {
        sequences.forEach { it.stop() }
    }

    override fun rewind() {
        elapsed = 0f
        sequences.forEach { it.rewind() }
    }
}

typealias Action = () -> Unit
private val noAction: Action = { }

typealias Updater = (dt: Float, alpha: Float, elapsed: Float, total: Float) -> Unit
private val noUpdates: Updater = { _, _, _, _ -> }


abstract class GameScene(contentIdentifier: String): GameObject(contentIdentifier) {
    abstract val timeline: Timeline

    protected fun install(timeline: Timeline) {
        managedContent.add(timeline)
    }
}

class TimelineMaster(
    parent: Scene,
    private val diagnosticsPanel: DiagnosticsPanel,
    timelineName: String
): GameObject("Timeline Master - ${parent.name} ($timelineName)") {

    override val managedContent = mutableListOf<ManagedContent>()

    val isRunning: Boolean
        get() = active?.isRunning ?: false

    val isPaused: Boolean
        get() = active?.isPaused ?: false

    val hasEnded: Boolean
        get() = active?.hasEnded ?: false

    val hasStopped: Boolean
        get() = active?.hasStopped ?: false

    private val timelines = mutableMapOf<Timeline, Boolean>()

    private var active: Timeline? = null

    private val elapsed: Float
        get() { return active?.elapsed ?: 0f }
    private val duration: Float
        get() { return active?.duration ?: 0f }

    fun register(timeline: Timeline) {
        info( "registering: ${timeline.contentIdentifier}")
        timelines[timeline] = true
    }

    fun start(timeline: Timeline) {
        timelines.forEach { if (it.value) { it.key.stop() } }
        timeline.start()
        active = timeline
    }

    fun pause(timeline: Timeline) {
        timeline.pause()
    }

    fun resume(timeline: Timeline) {
        timeline.resume()
    }

    fun stop(timeline: Timeline) {
        timeline.stop()
    }

    fun rewind(timeline: Timeline) {
        timeline.rewind()
    }

    override fun update(dt: Float) {
        diagnosticsPanel.sceneName = active?.contentIdentifier ?: "Unknown"
        diagnosticsPanel.shotName = active?.contentIdentifier ?: "???"
        diagnosticsPanel.shotElapsed = active?.elapsed ?: 0f
        diagnosticsPanel.shotTotal = active?.duration ?: Float.POSITIVE_INFINITY
        diagnosticsPanel.shotProgress = active?.alpha ?: 0f

        timelines.forEach { if (it.value) { it.key.update(dt) } }
    }

    override fun render(batch: SpriteBatch) {
        timelines.forEach { if (it.value) { it.key.render(batch) } }
    }



}

class Timeline(
    timeLineName: String,
    master: TimelineMaster,
    private val lanes: List<Lane>,
    private val onStart: Action = noAction,
    private val onUpdate: Updater = noUpdates,
    private val onPause: Action = noAction,
    private val onResume: Action = noAction,
    private val onStop: Action = noAction,
    private val onRewind: Action = noAction,
    private val onDone: Action = noAction
): GameObject("Timeline - $timeLineName"), Playback, TimeFrame {

    init {
        master.register(this)
    }

    private val panel: Panel = panelOf(
        caption = "$timeLineName - Initial Panel",
        left = 0f, bottom = 0f,
        dimension = 1092f, type = PanelDimension.WIDTH
    )

    override val managedContent = mutableListOf<ManagedContent>(
        panel
    )

    override var elapsed = 0f
    override val duration: Float
        get() { return lanes.maxOf { it.duration } }

    override val isRunning: Boolean
        get () = if (lanes.isEmpty()) { false } else { lanes.any { it.isRunning } }

    override val isPaused: Boolean
        get() = if (lanes.isEmpty()) { false } else { lanes.all { it.isPaused } }

    override val hasEnded: Boolean
        get() = if (lanes.isEmpty()) { false } else { lanes.all { it.hasEnded } }

    override val hasStopped: Boolean
        get() = if (lanes.isEmpty()) { false } else { lanes.all { it.hasStopped } }

    override fun update(dt: Float) {
        if (isRunning) {
            panel.update(dt)
            lanes.forEach { it.update(dt) }
            onUpdate(dt, alpha, elapsed, duration)
            if (!isRunning) {
                onDone()
            }
        }
    }

    override fun render(batch: SpriteBatch) {
        if (isRunning) {
            lanes.forEach { it.render(batch) }
            panel.render(batch)
        }
    }

    override fun start() {
        lanes.forEach { it.start() }
        onStart()
    }

    override fun pause() {
        lanes.forEach { it.pause() }
        onPause()
    }

    override fun resume() {
        lanes.forEach { it.resume() }
        onResume()
    }

    override fun stop() {
        lanes.forEach { it.stop() }
        onStop()
    }

    override fun rewind() {
        lanes.forEach { it.rewind() }
        onRewind()
    }
}