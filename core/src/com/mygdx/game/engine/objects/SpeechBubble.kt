package com.mygdx.game.engine.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.engine.Config
import com.mygdx.game.engine.DebugRenderer
import com.mygdx.game.engine.Tween
import com.mygdx.game.engine.TweenFunction
import com.mygdx.game.engine.stdx.DynamicComponent
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.stdx.Render
import com.mygdx.game.engine.stdx.Update
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


typealias CoordinateProjector = (x: Float, y: Float) -> Vector2

class SpeechBubblePivot(
    var x: Float,
    var y: Float,
    val sceneToOverlay: CoordinateProjector
): GameObject {
    private val debugRenderer = DebugRenderer(Config.DEBUG_RENDER_SHOW_PIVOTS_POINTS)
    private val hPadding = 460f
    private val vPadding = 120f

    var boxedX: Float = x
        get() {
            val overlay = sceneToOverlay(x, y)
            return max(hPadding, min(overlay.x, Config.WINDOW_WIDTH.toFloat() - hPadding))
        }

    var boxedY: Float = y
        get() {
            val overlay = sceneToOverlay(x, y)
            return max(vPadding + 120f, min(overlay.y, Config.WINDOW_HEIGHT.toFloat() - vPadding))
        }


    override fun create() {
        debugRenderer.create()
    }

    override fun destroy() {
        debugRenderer.destroy()
    }

    override fun render(batch: SpriteBatch) {
        batch.end()
        debugRenderer.line(
            batch.projectionMatrix,
            x - 15, y - 15, x + 15, y + 15,
            4
        )
        debugRenderer.line(
            batch.projectionMatrix,
            x - 15, y + 15, x + 15, y - 15,
            4
        )
        batch.begin()
    }
}

private enum class SpeechBubbleState {
    BLEND_IN,
    DISPLAY,
    BLEND_OUT,
    GONE
}

class SpeechBubble(
    private val textColor: Color,
    private val pivot: SpeechBubblePivot
): Update, Render, DynamicComponent {

    private var displayText: String? = null
    private var font: BitmapFont? = null
    private var shapeRender: ShapeRenderer? = null
    private var layout: GlyphLayout? = null
    private var state = SpeechBubbleState.GONE

    private var displayDuration: Float = 0f
    private var displayElapsed: Float = 0f

    private var done: () -> Unit = {}

    private var sb = StringBuilder()

    var isDone: Boolean = true
        get() { return state == SpeechBubbleState.GONE }

    private var boxAlpha = 0f
    private var boxTween = Tween(
        duration = 0.25f,
        origin = { 0f },
        target = { 1f },
        interpolate = TweenFunction.EASE_IN_OUT.fn,
        onInit = { boxAlpha = 0f },
        onUpdate = {
            boxAlpha = min(1f, it)
        },
        onDone = {
            if (state == SpeechBubbleState.BLEND_IN) {
                state = SpeechBubbleState.DISPLAY
                textTween.start()
            } else if (state == SpeechBubbleState.BLEND_OUT) {
                println("Done")
                done()
                done = {}
                state = SpeechBubbleState.GONE
                if (backlog.isNotEmpty()) {
                    println("Take next: ${backlog.size}")
                    val args = backlog.removeFirst()
                    displayText(args.text, args.duration, args.onDone)
                }
            }
        }
    )

    private var textAlpha = 0f
    private var textTween = Tween(
        duration = 0.2f,
        origin = { 0f },
        target = { 1f },
        interpolate = TweenFunction.EASE_IN_OUT.fn,
        onInit = { textAlpha = 0f },
        onUpdate = {
            textAlpha = min(1f, it)
        }
    )

    override fun create() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/backissue_reg.otf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.genMipMaps = true
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
        parameter.magFilter = Texture.TextureFilter.Linear
        parameter.size = 28
        parameter.color = textColor
        font = generator.generateFont(parameter)
        generator.dispose()

        shapeRender = ShapeRenderer()
    }

    override fun destroy() {
        font!!.dispose()
        shapeRender!!.dispose()
    }

    private val backlog: ArrayDeque<SayArgs> = ArrayDeque()

    data class SayArgs(
        val text: String,
        val duration: Float,
        val onDone: () -> Unit = {}
    )

    fun say(text: String, duration: Float, onDone: () -> Unit = {}) {
        val blocks = text.split("\n")
        for (i in blocks.indices) {
            breakBlock(
                blocks[i],
                blocks[i].length / text.length.toFloat() * duration,
                if (i + 1 == blocks.size) {
                    onDone
                } else {
                    {}
                }
            )
        }
    }

    private fun breakBlock(text: String, duration: Float, onDone: () -> Unit = {}) {
        sb.clear()
        val words = text.split(" ")
        var lines = 1;
        for (word in words) {
            sb.append(word)
            sb.append(" ")
            if (sb.length > lines * 35) {
                sb.append("\n")
                lines++
            }
        }
        schedule(
            sb.toString(),
            duration,
            onDone
        )
    }

    private fun schedule(text: String, duration: Float, onDone: () -> Unit = {}) {
        if (text.isNotBlank()) {
            println("Length ${text.length}")
            val actualDuration = max(1.3f, duration)
            if (isDone) {
                displayText(text, actualDuration, onDone)
                println("say: '$text'")
            } else {
                backlog.add(SayArgs(text, actualDuration, onDone))
                println("delay: '$text'")
            }
        }
    }

    private fun displayText(text: String, duration: Float, onDone: () -> Unit = {}) {
        displayText = text
        displayDuration = duration
        displayElapsed = 0f
        layout = GlyphLayout(font, text)
        state = SpeechBubbleState.BLEND_IN
        done = onDone
        boxTween.start()
    }

    fun abort() {
        startBlendOut()
    }

    override fun update(dt: Float) {
        boxTween.update(dt)
        textTween.update(dt)

        if (state == SpeechBubbleState.DISPLAY) {
            displayElapsed += dt
            if (displayElapsed > displayDuration) {
                startBlendOut()
            }
        }
    }

    override fun render(batch: SpriteBatch) = when (state) {
        SpeechBubbleState.BLEND_IN -> blendIn(batch)
        SpeechBubbleState.DISPLAY -> display(batch)
        SpeechBubbleState.BLEND_OUT -> blendOut(batch)
        SpeechBubbleState.GONE -> { }
    }

    private fun blendIn(batch: SpriteBatch) = blend(batch, boxAlpha, true)
    private fun blendOut(batch: SpriteBatch) = blend(batch, 1 - boxAlpha, false)

    private fun blend(batch: SpriteBatch, progress: Float, up: Boolean) {

        println("x: ${pivot.boxedX}, y: ${pivot.boxedY}")

        batch.end()

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRender!!.projectionMatrix = batch.projectionMatrix
        shapeRender!!.begin(ShapeRenderer.ShapeType.Filled)

        val pitch = 15f
        val horizontal = if (up) {
            -pitch
        } else {
            0f
        } + (boxAlpha * boxAlpha * pitch)

        val halfWidth = (layout!!.width) / 2f
        val halfHeight = (layout!!.height) / 2f

        val width = progress * (layout!!.width)
        val left = pivot.boxedX + layout!!.width / 2f - (progress * layout!!.width / 2f)
        val height = progress * (layout!!.height)
        val bottom = pivot.boxedY + layout!!.height / 2f - (progress * layout!!.height / 2f) + horizontal

        val xCorrection = computeCorrectionX(halfWidth)
        val yCorrection = computeCorrectionY(halfHeight)

        shapeRender!!.roundedRect(
            0f, 0f, 0f, progress * 0.80f,
            left - halfWidth - 30f + xCorrection, bottom - halfHeight - 50f + yCorrection, width + 60f, height + 50f, 12f
        )

        shapeRender!!.end()

        batch.begin()
    }

    private fun display(batch: SpriteBatch) {
        batch.end()

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shapeRender!!.projectionMatrix = batch.projectionMatrix
        shapeRender!!.begin(ShapeRenderer.ShapeType.Filled)

        val halfWidth = (layout!!.width) / 2f
        val halfHeight = (layout!!.height) / 2f

        val xCorrection = computeCorrectionX(halfWidth)
        val yCorrection = computeCorrectionY(halfHeight)

        shapeRender!!.roundedRect(
            0f, 0f, 0f, 0.80f,
            pivot.boxedX - halfWidth - 30f + xCorrection, pivot.boxedY - halfHeight - 50f + yCorrection, layout!!.width + 60f, layout!!.height + 50f, 12f
        )

        shapeRender!!.end()

        batch.begin()
        font!!.color.a = textAlpha
        font!!.draw(batch, displayText, pivot.boxedX - halfWidth + xCorrection, pivot.boxedY + halfHeight - 25f + yCorrection)
    }

    private fun startBlendOut() {
        if (state == SpeechBubbleState.BLEND_IN || state == SpeechBubbleState.DISPLAY) {
            boxTween.start()
            state = SpeechBubbleState.BLEND_OUT
        }
    }

    private fun ShapeRenderer.roundedRect(
        r: Float, g: Float, b: Float, a: Float,
        x: Float, y: Float, width: Float, height: Float, radius: Float,
        samples: Int = 7,
    ) {
        shapeRender!!.end()
        shapeRender!!.begin(ShapeRenderer.ShapeType.Filled)

        var a = a / samples * 2f

        for (i in 1 .. samples) {
            setColor(r, g, b, a - (a * (i/samples.toFloat())))

            // Central rectangle
            rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius)

            // Four side rectangles, in clockwise order
            rect(x + radius, y, width - 2 * radius, radius)
            rect(x + width - radius, y + radius, radius, height - 2 * radius)
            rect(x + radius, y + height - radius, width - 2 * radius, radius)
            rect(x, y + radius, radius, height - 2 * radius)

            // Four arches, clockwise too
            val segments = 2.0.pow(i).toInt()
            arc(x + radius, y + radius, radius, 180f, 90f, segments)
            arc(x + width - radius, y + radius, radius, 270f, 90f, segments)
            arc(x + width - radius, y + height - radius, radius, 0f, 90f, segments)
            arc(x + radius, y + height - radius, radius, 90f, 90f, segments)
        }

        shapeRender!!.end()
        shapeRender!!.begin(ShapeRenderer.ShapeType.Filled)
    }

    private fun computeCorrectionX(halfWidth: Float): Float {
        val margin = 250
        val outLeft = pivot.boxedX - halfWidth
        val ourRight = pivot.boxedX + halfWidth
        return if (outLeft < margin) {
            margin - outLeft
        } else if (ourRight > Config.WINDOW_WIDTH - margin) {
            -(ourRight - (Config.WINDOW_WIDTH - margin))
        } else {
            0f
        }
    }

    private fun computeCorrectionY(halfHeight: Float): Float {
        val margin = 250
        val outTop = pivot.boxedY + halfHeight
        val ourBottom = pivot.boxedY - halfHeight
        return if (ourBottom < margin) {
            margin - ourBottom
        } else if (outTop > Config.WINDOW_HEIGHT) {
            -(outTop - (Config.WINDOW_HEIGHT))
        } else {
            0f
        }
    }
}