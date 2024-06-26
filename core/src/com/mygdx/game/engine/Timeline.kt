package com.mygdx.game.engine

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.DiagnosticsPanel
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.stdx.Render
import com.mygdx.game.engine.stdx.Update
import com.mygdx.game.engine.utils.info
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

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
    fun done()
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

    override fun done() {
        stop()
        onDone()
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

    override fun done() {
        sequences.forEach { it.done() }
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

class EmptyGameScene: GameScene(
    contentIdentifier = "empty game scene"
) {
    override val firstPanel = panelOf(
        caption = "Empty Game Scene Panel",
        left = 0f, bottom = 0f,
        dimension = Engine.canvas.surface.width, type = PanelDimension.WIDTH
    )

    override val cutInEffect = CutEffectDescriptor.cut()

    override val timeline = Timeline(
        timeLineName = "Empty Game Scene Timeline"
    )

    override val managedContent = mutableListOf<ManagedContent>(
        firstPanel
    )

}

abstract class GameScene(contentIdentifier: String): GameObject(contentIdentifier) {

    abstract val firstPanel: Panel
    abstract val cutInEffect: CutEffectDescriptor
    abstract val timeline: Timeline

    protected fun install(timeline: Timeline) {
        managedContent.add(timeline)
    }
}

class GameSceneComposer(
    parentName: String,
    private val diagnosticsPanel: DiagnosticsPanel?,
    timelineName: String,
    private val camera: SceneCamera,
    private val cutOnStart: Boolean = true,
    private val autoStart: Boolean = true,
    private val initial: GameScene?,
    private val others: List<GameScene>
): GameObject("Timeline Master - $parentName ($timelineName)") {

    private val allScenes = if (initial != null) {
        (listOf(initial) + others)
    } else {
        listOf()
    }

    private val index: Map<KClass<*>, GameScene> = allScenes.associateBy {
        it::class
    }

    override val managedContent: MutableList<ManagedContent> = (
        allScenes +
        mutableListOf(
            managedContentOf(
                id = "Diagnostics setup",
                load = {
                    diagnosticsPanel?.visible = true
                },
                unload = {
                    diagnosticsPanel?.visible = false
                }
            ),
            managedContentOf(
                id = "Timeline setup",
                load = {
                    allScenes.forEach {
                        register(it::class)
                    }
                    if (autoStart && initial != null) {
                        start(initial::class, cutOnStart)
                    }
                },
                unload = { }
            )
        )
    ).toMutableList()

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

    fun register(sceneId: KClass<*>) {
        val scene = index[sceneId]!!
        info( "registering: ${scene.timeline.id}")
        timelines[scene.timeline] = true
    }

    fun start(sceneId: KClass<*>, cut: Boolean = true) {
        val scene = index[sceneId]!!
        timelines.forEach { if (it.value) { it.key.stop() } }
        scene.timeline.start()
        active = scene.timeline
        if (cut) {
            camera.cut(
                scene.firstPanel,
                scene.cutInEffect
            )
        }
    }

    fun pause() {
        active?.pause()
    }

    fun resume() {
        active?.resume()
    }

    fun stop() {
        active?.stop()
    }

    fun rewind() {
        active?.rewind()
    }

    override fun update(dt: Float) {
        diagnosticsPanel?.sceneName = active?.id ?: "Unknown"
        diagnosticsPanel?.shotName = "???"
        diagnosticsPanel?.shotElapsed = active?.elapsed ?: 0f
        diagnosticsPanel?.shotTotal = active?.duration ?: Float.POSITIVE_INFINITY
        diagnosticsPanel?.shotProgress = active?.alpha ?: 0f

        allScenes.forEach { it.update(dt) }
        timelines.forEach { if (it.value) { it.key.update(dt) } }
    }

    override fun render(batch: SpriteBatch) {
        allScenes.forEach { it.render(batch) }
        timelines.forEach { if (it.value) { it.key.render(batch) } }
    }



}

class Timeline(
    timeLineName: String,
    private val lanes: List<Lane> = emptyList(),
    private val onStart: Action = noAction,
    private val onUpdate: Updater = noUpdates,
    private val onPause: Action = noAction,
    private val onResume: Action = noAction,
    private val onStop: Action = noAction,
    private val onRewind: Action = noAction,
    private val onDone: Action = noAction
): GameObject("$timeLineName"), Playback, TimeFrame {

    override val managedContent = mutableListOf<ManagedContent>()

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
            if (!isPaused) {
                elapsed += dt
            }
            lanes.forEach { it.update(dt) }
            onUpdate(dt, alpha, elapsed, duration)
            if (!isRunning) {
                onDone()
            }
        }
    }

    override fun render(batch: SpriteBatch) {
        lanes.forEach { it.render(batch) }
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
        elapsed = 0f
        lanes.forEach { it.stop() }
        onStop()
    }

    override fun done() {
        elapsed = 0f
        lanes.forEach { it.done() }
        onDone()
    }

    override fun rewind() {
        lanes.forEach { it.rewind() }
        onRewind()
    }
}