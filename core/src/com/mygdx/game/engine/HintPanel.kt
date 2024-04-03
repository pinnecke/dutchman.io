package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject
import kotlin.math.max

class HintPanel(
    private val effects: ScenePostEffects,
    private val getCamera: () -> OrthographicCamera,
    private val panelWidth: Float = 965f
): GameObject(
    contentIdentifier = "Hint Panel"
) {
    private var shapeRenderer: ShapeRenderer? = null
    private var font: BitmapFont? = null
    private var camera: OrthographicCamera? = null
    private var displayText: String = ""

    private val panelResizer = Tween(
        id = "hint panel resizer",
        create = { 0f }
    )

    private val panelBlender = Tween(
        id = "hint panel blender",
        create = { 0f }
    )

    private val textBlender = Tween(
        id = "hint text blender",
        create = { 0f }
    )

    private enum class State {
        BLEND_IN,
        BLEND_OUT,
        SHOWN,
        HIDDEN
    }

    private var state = State.HIDDEN

    private var zoom: Float = 0f

    val isShown: Boolean
        get() { return state == State.SHOWN || state == State.BLEND_IN }

    val isNotShown: Boolean
        get() { return state == State.HIDDEN || state == State.BLEND_OUT }

    override val managedContent = mutableListOf(
        managedContentOf(
            id = "Shape Renderer",
            load = {
                shapeRenderer = ShapeRenderer()
                shapeRenderer!!.color = Color.BLACK
            },
            unload = {
                shapeRenderer!!.dispose()
                shapeRenderer = null
            }
        ),
        managedContentOf(
            id = "Font",
            load = {
                val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/backissue_reg.otf"))
                val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
                parameter.genMipMaps = true
                parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
                parameter.magFilter = Texture.TextureFilter.Linear
                parameter.size = 28
                parameter.color = Color.WHITE
                font = generator.generateFont(parameter)
                generator.dispose()
            },
            unload = {
                font!!.dispose()
            }
        ),
        managedContentOf(
            id = "Camera setup",
            load = {
                camera = getCamera()
                zoom = camera!!.zoom
            },
            unload = { }
        ),
        panelResizer,
        panelBlender,
        textBlender
    )



    override fun update(dt: Float) {
        if (state != State.HIDDEN) {
            panelResizer.update(dt)
            panelBlender.update(dt)
            textBlender.update(dt)

            camera!!.zoom = zoom + (panelResizer.amount * 0.10f)
        }
    }

    override fun render(batch: SpriteBatch) {
        if (shapeRenderer != null) {
            with(shapeRenderer!!) {
                batch.end()
                projectionMatrix = batch.projectionMatrix

                Gdx.gl.glEnable(GL20.GL_BLEND)
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

                begin(ShapeRenderer.ShapeType.Filled)
                setColor(color.r, color.g, color.b, max(0f, panelBlender.amount - 0.4f))

                val actualWidth = max(0f, panelResizer.amount - 0.2f) * panelWidth

                rect(
                    Engine.canvas.surface.width - actualWidth, 0f,
                    actualWidth, Engine.canvas.surface.height
                )

                end()
                batch.begin()

                font!!.setColor(font!!.color.r, font!!.color.g, font!!.color.b, textBlender.amount)
                font!!.draw(
                    batch,
                    displayText,
                    Engine.canvas.surface.width - panelWidth + 237f,
                    Engine.canvas.surface.height - 250f
                )
            }
        }
    }

    fun show(
        text: String
    ) {
        if (isNotShown) {
            zoom = camera!!.zoom

            panelResizer.start(
                amount = 1f,
                duration = 0.7f,
                tween = TweenFunction.EASE_IN_OUT,
                onStart = {
                    state = State.BLEND_IN
                    effects.vignette.enabled = true
                    effects.vignette.start(
                        amount = 0.6f,
                        duration = 0.6f
                    )
                    displayText = text
                },
                onDone = {
                    state = State.SHOWN
                    textBlender.start(
                        amount = 1.0f,
                        duration = 0.3f
                    )
                }
            )
            panelBlender.start(
                amount = 1f,
                duration = 0.4f,
                tween = TweenFunction.EASE_IN,
            )
        }
    }

    fun hide() {
        if (isShown) {
            textBlender.start(
                amount = 0.0f,
                duration = 0.4f,
                onDone = {
                    panelResizer.start(
                        amount = 0f,
                        duration = 1.0f,
                        tween = TweenFunction.EASE_IN_OUT,
                        onStart = {
                            state = State.BLEND_OUT
                            effects.vignette.start(
                                amount = 0.0f,
                                duration = 0.6f
                            )

                        },
                        onDone = {
                            state = State.HIDDEN
                            effects.vignette.enabled = false

                        }
                    )
                    panelBlender.start(
                        amount = 0f,
                        duration = 0.6f,
                        tween = TweenFunction.EASE_IN_OUT
                    )
                }
            )
        }
    }

    fun reset() {
        panelResizer.stop()
        panelBlender.stop()
        state = State.HIDDEN
        camera!!.zoom = zoom
    }
}