package com.mygdx.game.engine.objects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.sprites.FrameList
import com.mygdx.game.engine.sprites.SpriteSheetManager
import com.mygdx.game.engine.stdx.*

data class Position(
    var left: Float,
    var bottom: Float
)

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
    val flipX: Boolean = false,
    val flipY: Boolean = false,
    var visible: Boolean = true,
    var animiate: Boolean = false,
    stretch: Float = 1f,
    compress: Float = 1f,
    opacity: Float = 1f,
    fps: Int = 24,
): GameObject("Decal - $name") {

    private var frameList = FrameList(
        id = "Decal '$name' Frames",
        sprite = name,
        sheets = sheets
    )

    private var moveTweenLeft: TweenProcessor? = null
    private var moveTweenBottom: TweenProcessor? = null
    private var moveLeftDone = false
    private var moveBottomDone = false
    private var originLeft: Float? = null
    private var originBottom: Float? = null
    private var targetLeft: Float? = null
    private var targetBottom: Float? = null

    val stretch = Tween(
        id ="stretch",
        init = stretch,
        create = { }
    )

    val compress = Tween(
        id ="compress",
        init = compress,
        create = { }
    )

    val scale = TweenMultiplexer(
        this.stretch,
        this.compress
    )

    val opacity = Tween(
        id ="opacity",
        init = opacity,
        create = { }
    )

    private val isMoveDone: Boolean
        get() { return moveLeftDone && moveBottomDone }

    private val decalBoundsDebugRenderer = DebugRenderer(
        id = "Decal '$name' Bounds",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_BOUNDS,
        shapeColor = Color.DARK_GRAY
    )

    private val movementDebugRenderer = DebugRenderer(
        id = "Decal '$name' Movement Target",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_MOVE_TARGET,
        shapeColor = Color.DARK_GRAY
    )

    private val opacityDebugRenderer = DebugRenderer(
        id = "Decal '$name' Opacity",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_OPACITY,
        shapeColor = Color.DARK_GRAY
    )

    private val opacityTargetDebugRenderer = DebugRenderer(
        id = "Decal '$name' Opacity Target",
        enabled = Config.DEBUG_RENDER_SHOW_DECAL_OPACITY,
        shapeColor = Color.RED
    )

    private val nameDebugRenderer = DebugRenderer(
        id = "Decal '$name' Name",
        enabled = Config.DEBUG_RENDER_SHOW_NAMES,
        shapeColor = Color.DARK_GRAY
    )

    private val scaleDebugRenderer = DebugRenderer(
        id = "Decal '$name' Scale",
        enabled = Config.DEBUG_RENDER_SHOW_SCALING,
        shapeColor = Color.DARK_GRAY
    )

    private val positionDebugRenderer = DebugRenderer(
        id = "Decal '$name' Position",
        enabled = Config.DEBUG_RENDER_SHOW_POSITION,
        shapeColor = Color.DARK_GRAY
    )

    override val managedContent = mutableListOf(
        frameList,
        movementDebugRenderer,
        decalBoundsDebugRenderer,
        opacityDebugRenderer,
        opacityTargetDebugRenderer,
        scaleDebugRenderer,
        positionDebugRenderer,
        nameDebugRenderer,
        this.stretch,
        this.compress,
        this.scale,
        this.opacity
    )

    val surface: Surface
        get() { return Surface(
                width = width.get { frameList.first.value.width.toFloat() },
                height = height.get { frameList.first.value.height.toFloat() }
            )
        }

    private var index: Int = 0
    private var iteration: Int = 0
    private val nextFrame = runRepeated(1/fps.toFloat()) {
        if (animiate && (iterations.isInfinite || iteration < iterations.get { 0 })) {
            nextFrame()
        }
    }

    fun reset() {
        index = 0
        iteration = 0
    }

    override fun update(dt: Float) {
        nextFrame.update(dt)
        moveTweenLeft?.update(dt)
        moveTweenBottom?.update(dt)
        stretch.update(dt)
        compress.update(dt)
        opacity.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        if (visible) {
            val frame = frameList[index].value
            val color = batch.color
            val scaledLeft = position.left + frame.width.toFloat() / 2f - ((stretch.amount * frame.width.toFloat()) / 2f)
            val scaledBottom = position.bottom + frame.height.toFloat() / 2f - ((compress.amount * frame.height.toFloat()) / 2f)
            batch.setColor(
                color.r,
                color.g,
                color.b,
                opacity.amount
            )
            batch.draw(
                frame,
                scaledLeft,
                scaledBottom,
                0f, 0f,
                stretch.amount * frame.width.toFloat(),
                compress.amount * frame.height.toFloat(),
                1f, 1f,
                0f,
                0, 0,
                frame.width, frame.height,
                flipX, flipY
            )
            batch.color = color

            decalBoundsDebugRenderer.render(batch) { renderer ->
                renderer.rect(
                    batch.projectionMatrix,
                    scaledLeft,
                    scaledBottom,
                    stretch.amount * frame.width.toFloat(),
                    compress.amount * frame.height.toFloat(),
                    1
                )
            }

            opacityDebugRenderer.render(batch) { renderer ->
                renderer.filled(
                    batch.projectionMatrix,
                    scaledLeft,
                    scaledBottom,
                    opacity.amount * stretch.amount * frame.width.toFloat(), 5f,
                    1
                )
            }

            opacityTargetDebugRenderer.render(batch) { renderer ->
                if (opacity.isTweening) {
                    val left = scaledLeft + (opacity.target * stretch.amount * frame.width.toFloat())
                    renderer.line(
                        batch.projectionMatrix,
                        left, scaledBottom,
                        left, scaledBottom + 5f,
                        5
                    )
                }
            }

            nameDebugRenderer.print(batch, name, scaledLeft + 8f, scaledBottom + compress.amount * frame.height - 8f)
            scaleDebugRenderer.print(batch, "s: ${"%.2f".format(stretch.amount)}%, c: ${"%.2f".format(compress.amount)}%", scaledLeft + 8f, scaledBottom + compress.amount * frame.height - 23f)
            positionDebugRenderer.print(batch, "x: ${"%.2f".format(position.left).padEnd(8, ' ')}, y: ${"%.2f".format(position.bottom).padEnd(8, ' ')}", scaledLeft + 8f, scaledBottom + 38f)
            positionDebugRenderer.print(batch, "w: ${"%.2f".format(stretch.amount * frame.width).padEnd(8, ' ')}, h:${"%.2f".format(compress.amount * frame.height).padEnd(8, ' ')}", scaledLeft + 8f, scaledBottom + 23f)

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
        if (index + 1 == frameList.size) {
            iteration++
        }
        index = (index + 1) % frameList.size
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
        moveTweenLeft = TweenProcessor(
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

        moveTweenBottom = TweenProcessor(
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

    private fun resetDebuggerMovement() {
        originLeft = null
        originBottom = null
        targetLeft = null
        targetBottom = null
    }



}