package com.mygdx.game.engine.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.sprites.Frame
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.stdx.*

fun centered(
    scene: Scene,
    surface: () -> Surface,
    offset: Auto<Position> = value(Position.default.copy())
): Auto<Position> = value(Position(
    left = scene.width.toFloat() / 2f - surface().width / 2f + offset.get { Position.default.copy() }.left,
    bottom = scene.height.toFloat() / 2f - surface().height / 2f + offset.get { Position.default.copy() }.bottom
))

data class Position(
    var left: Float,
    var bottom: Float
) {
    companion object {
        val default: Position = Position(0f, 0f)
    }
}

data class Surface(
    val width: Float,
    val height: Float
)

private val decalAutoPosition = Position(
    left = 0f,
    bottom = 0f
)

class Decal(
    private val name: String,
    var position: Position = decalAutoPosition.copy(),
    private val sheets: () -> SpriteSheetManager,
    private val width: Auto<Float> = auto(),
    private val height: Auto<Float> = auto(),
    private val iterations: Auto<Int> = infinite(),
    val scale: Float = 1.0f,
    val flipX: Boolean = false,
    val flipY: Boolean = false,
    var visible: Boolean = true,
    var animiate: Boolean = false,
    var opacity: Float = 1f,
    var fps: Int = 24,
): GameObject("Decal - $name") {

    private var frames: List<Frame> = listOf()

    private var moveTweenLeft: Tween? = null
    private var moveTweenBottom: Tween? = null
    private var moveLeftDone = false
    private var moveBottomDone = false
    private var originLeft: Float? = null
    private var originBottom: Float? = null
    private var targetLeft: Float? = null
    private var targetBottom: Float? = null

    private var opacityTween: Tween? = null
    private var targetOpacity: Float? = null

    private var highlightTween: Tween? = null
    private var highlightValue = 0f

    private val isMoveDone: Boolean
        get() { return moveLeftDone && moveBottomDone }

    private val decalBoundsDebugRenderer = DebugRenderer(
        renderContextName = "Decal '$name' Bounds",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_BOUNDS,
        renderColor = Color.DARK_GRAY
    )

    private val movementDebugRenderer = DebugRenderer(
        renderContextName = "Decal '$name' Movement Target",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_MOVE_TARGET,
        renderColor = Color.DARK_GRAY
    )

    private val opacityDebugRenderer = DebugRenderer(
        renderContextName = "Decal '$name' Opacity",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_OPACITY,
        renderColor = Color.DARK_GRAY
    )

    private val opacityTargetDebugRenderer = DebugRenderer(
        renderContextName = "Decal '$name' Opacity Target",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_OPACITY,
        renderColor = Color.RED
    )

    override val managedContent = mutableListOf(
        managedContentOf(
            contentIdentifier = "Frames",
            load = {
                frames = sheets().get(name)
            },
            unload = {
                frames.forEach {
                    it.release()
                }
            }
        ),
        movementDebugRenderer,
        decalBoundsDebugRenderer,
        opacityDebugRenderer,
        opacityTargetDebugRenderer
    )

    val surface: Surface
        get() { return Surface(
                width = width.get { frames[0].value.width.toFloat() },
                height = height.get { frames[0].value.height.toFloat() }
            )
        }

    private var index: Int = 0
    private var iteration: Int = 0
    private val nextFrame = runRepeated(1/fps.toFloat()) {
        if (animiate && (iterations.isInfinite || iteration < iterations.get { 0 })) {
            nextFrame()
        }
    }
    private val currentFrame: Texture
        get() { return frames[index].value }

    fun reset() {
        index = 0
        iteration = 0
    }

    override fun update(dt: Float) {
        nextFrame.update(dt)
        moveTweenLeft?.update(dt)
        moveTweenBottom?.update(dt)
        opacityTween?.update(dt)
        highlightTween?.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        if (visible) {
            val frame = frames[index].value
            val color = batch.color
            batch.setColor(
                color.r,
                color.g,
                color.b,
                opacity
            )
            batch.draw(
                frame,
                position.left, position.bottom,
                0f, 0f,
                surface.width,
                surface.height,
                scale, scale,
                0f,
                0, 0,
                frame.width, frame.height,
                flipX, flipY
            )
            batch.color = color

            decalBoundsDebugRenderer.render(batch) { renderer ->
                renderer.rect(
                    batch.projectionMatrix,
                    position.left,
                    position.bottom,
                    currentFrame.width.toFloat(), currentFrame.height.toFloat(),
                    1
                )
            }

            opacityDebugRenderer.render(batch) { renderer ->
                renderer.filled(
                    batch.projectionMatrix,
                    position.left,
                    position.bottom,
                    opacity * currentFrame.width.toFloat(), 5f,
                    1
                )
            }

            opacityTargetDebugRenderer.render(batch) { renderer ->
                if (targetOpacity != null) {
                    val left = position.left + (targetOpacity ?: 0f) * currentFrame.width.toFloat()
                    renderer.line(
                        batch.projectionMatrix,
                        left, position.bottom,
                        left, position.bottom + 5f,
                        5
                    )
                }
            }

            movementDebugRenderer.render(batch) { renderer ->
                if (originLeft != null && originBottom != null && targetLeft != null && targetBottom != null) {
                    renderer.rect(
                        batch.projectionMatrix,
                        (originLeft ?: 0f) - 10f,
                        (originBottom ?: 0f) - 10f,
                        10f, 10f, 1
                    )
                    renderer.rect(
                        batch.projectionMatrix,
                        (targetLeft ?: 0f) - 10f,
                        (targetBottom ?: 0f) - 10f,
                        10f, 10f, 1
                    )
                    renderer.line(
                        batch.projectionMatrix,
                        (originLeft ?: 0f) - 10f,
                        (originBottom ?: 0f) - 10f,
                        (targetLeft ?: 0f) - 10f,
                        (targetBottom ?: 0f) - 10f
                    )
                }
            }
        }
    }

    fun nextFrame() {
        if (index + 1 == frames.size) {
            iteration++
        }
        index = (index + 1) % frames.size
    }

    fun move(
        left: Float,
        bottom: Float
    ) {
        position.left = left
        position.bottom = bottom
    }

    fun move(
        left: Float,
        bottom: Float,
        duration: Float = 0f,
        tween: TweenFunction = TweenFunction.EASE_IN_OUT,
        onDone: () -> Unit = { },
    ) {
        moveTweenLeft = Tween(
            duration = duration,
            onInit = {
                moveLeftDone = false
                originLeft = position.left
                targetLeft = left
            },
            origin = { position.left },
            target = { left },
            onUpdate = { position.left = it },
            interpolate = tween.fn,
            onDone = {
                moveLeftDone = true
                if (isMoveDone) {
                    resetDebuggerMovement()
                    onDone()
                }
            }
        )
        moveTweenLeft?.start()

        moveTweenBottom = Tween(
            duration = duration,
            onInit = {
                moveBottomDone = false
                originBottom = position.bottom
                targetBottom = bottom
            },
            origin = { position.bottom },
            target = { bottom },
            onUpdate = { position.bottom = it },
            interpolate = tween.fn,
            onDone = {
                moveBottomDone = true
                if (isMoveDone) {
                    resetDebuggerMovement()
                    onDone()
                }
            }
        )
        moveTweenBottom?.start()
    }


    fun opacity(
        amount: Float,
        duration: Float,
        tween: TweenFunction,
        onDone: () -> Unit = { }
    ) {
        opacityTween = Tween(
            duration = duration,
            onInit = {
                targetOpacity = amount
            },
            origin = { opacity },
            target = { amount },
            onUpdate = { opacity = it },
            interpolate = tween.fn,
            onDone = {
                targetOpacity = null
                onDone()
            }
        )
        opacityTween?.start()
    }


    fun highlight(
        enabled: Boolean,
        duration: Float,
        tween: TweenFunction,
        onDone: () -> Unit = { }
    ) {
        highlightTween = Tween(
            duration = duration,
            origin = { highlightValue },
            target = { if (enabled) { 1.0f } else { 0.0f } },
            onUpdate = { highlightValue = it },
            interpolate = tween.fn,
            onDone = {
                onDone()
            }
        )
        highlightTween?.start()
    }

    private fun resetDebuggerMovement() {
        originLeft = null
        originBottom = null
        targetLeft = null
        targetBottom = null
    }



}