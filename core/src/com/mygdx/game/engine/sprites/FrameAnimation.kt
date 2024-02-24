package com.mygdx.game.engine.sprites

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.collision.BoundingBox
import com.mygdx.game.engine.Screen
import com.mygdx.game.engine.stdx.*

fun centered(
    screen: Screen,
    surface: () -> Surface,
    offset: Auto<Position> = value(Position.default)
): Auto<Position> = value(Position(
    left = { screen.width.toFloat() / 2f - surface().width / 2f + offset.get { Position.default }.left() },
    bottom = { screen.height.toFloat() / 2f - surface().height / 2f + offset.get { Position.default }.bottom()  }
))

data class Position(
    val left: () -> Float,
    val bottom: () -> Float
) {
    companion object {
        val default: Position = Position({ 0f }, { 0f })
    }
}

data class Surface(
    val width: Float,
    val height: Float
)

class FrameAnimation(
    private val name: String,
    var position: Auto<Position> = auto(),
    private val sheets: () -> SpriteSheetManager,
    private val width: Auto<Float> = auto(),
    private val height: Auto<Float> = auto(),
    private val iterations: Auto<Int> = infinite(),
    private val scale: Float = 1.0f,
    private val flipX: Boolean = false,
    private val flipY: Boolean = false,
    fps: Int = 24,
): GameComponent {
    val surface: Surface
        get() { return Surface(
            width = width.get { frames[0].value.width.toFloat() },
            height = height.get { frames[0].value.height.toFloat() }
        )}

    private var running = false
    private var index: Int = 0
    private var frames: List<Frame> = listOf()
    private var iteration: Int = 0
    private val nextFrame = runRepeated(1/fps.toFloat()) {
        if (running && (iterations.isInfinite || iteration < iterations.get { 0 })) {
            if (index + 1 == frames.size) {
                iteration++
            }
            index = (index + 1) % frames.size
        }
    }

    fun start() {
        running = true
    }

    override fun update(dt: Float) {
        nextFrame.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        val frame = frames[index].value
        val position = position.get { Position.default }
        batch.draw(
            frame,
            position.left(), position.bottom(),
            0f, 0f,
            surface.width,
            surface.height,
            scale, scale,
            0f,
            0, 0,
            frame.width, frame.height,
            flipX, flipY
        )
    }

    override fun load() {
        frames = sheets().get(name)
    }

    override fun unload() {
        frames.forEach {
            it.release()
        }
    }
}