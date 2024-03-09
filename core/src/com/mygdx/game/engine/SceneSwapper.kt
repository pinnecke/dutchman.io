package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.utils.info

enum class SwapState {
    SWAP_INIT,
    SWAP_BLENDING_OUT,
    SWAP_START_UNLOAD_OLD,
    SWAP_UNLOADING_OLD,
    SWAP_START_LOAD_NEW,
    SWAP_LOADING_NEW,
    SWAP_BLENDING_IN,
    SWAP_DONE
}

enum class AnimationState {
    BLEND_OUT,
    BLEND_IN
}

class ScummBlendAnimation: GameObject("Scumm Blend Animation") {

    private var viewport: Viewport? = null
    private var shapeRenderer: ShapeRenderer? = null

    override val managedContent = mutableListOf(
        managedContentOf(
            contentIdentifier = "Camera setup",
            load = {
                viewport = StretchViewport(Config.WINDOW_WIDTH.toFloat(), Config.WINDOW_HEIGHT.toFloat())
                viewport!!.apply()
            },
            unload = { }
        ),
        managedContentOf(
            contentIdentifier = "Shape Renderer",
            load = {
                shapeRenderer = ShapeRenderer()
                shapeRenderer!!.color = Color.BLACK
            },
            unload = {
                shapeRenderer!!.dispose()
            }
        )
    )

    var hasFinished: Boolean = true

    private val duration = 0.5f
    private var state = AnimationState.BLEND_OUT
    private var elapsed = 0f

    fun playBlendOut() {
        state = AnimationState.BLEND_OUT
        elapsed = 0f
        hasFinished = false
    }

    fun playBlendIn() {
        state = AnimationState.BLEND_IN
        elapsed = 0f
        hasFinished = false
    }

    override fun update(dt: Float) {
        if (elapsed >= duration) {
            hasFinished = true
        }

        if (!hasFinished) {
            elapsed += dt

            when (state) {
                AnimationState.BLEND_OUT -> {

                }
                AnimationState.BLEND_IN -> {

                }
            }
        }
    }

    fun render() {
        viewport!!.update(Gdx.graphics.width, Gdx.graphics.height, true)
        viewport!!.apply()
        val windowWidth = Config.WINDOW_WIDTH
        val windowHeight = Config.WINDOW_HEIGHT
        shapeRenderer!!.projectionMatrix = viewport!!.camera.combined
        val extendHorizontal = 0f
        val extendVertical = 0f

        if (!hasFinished) {

            val alpha = TweenFunction.EASE_OUT.fn(elapsed / duration, 0f, 1f)
            shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)

            when (state) {
                AnimationState.BLEND_OUT -> {
                    val horizontal = alpha / 2f * (windowWidth + extendHorizontal)
                    val vertical = alpha / 2f * (windowHeight + extendVertical)

                    shapeRenderer!!.rect(
                        0f,
                        0f,
                        horizontal,
                        (windowHeight + extendVertical)
                    )
                    shapeRenderer!!.rect(
                        (windowWidth + extendHorizontal) - horizontal,
                        0f,
                        horizontal,
                        (windowHeight + extendVertical)
                    )
                    shapeRenderer!!.rect(
                        0f,
                        0f,
                        (windowWidth + extendHorizontal),
                        vertical
                    )
                    shapeRenderer!!.rect(
                        0f,
                        (windowHeight + extendVertical) - vertical,
                        (windowWidth + extendHorizontal),
                        vertical
                    )
                }
                AnimationState.BLEND_IN -> {
                    shapeRenderer!!.rect(
                         alpha / 2f * (windowWidth + extendHorizontal),
                        alpha / 2f * (windowHeight + extendVertical),
                        (windowWidth + extendHorizontal) - (alpha * (windowWidth + extendHorizontal)),
                        (windowHeight + extendVertical) - (alpha * (windowHeight + extendVertical))
                    )
                }
            }

            shapeRenderer!!.end()
        }
    }
}

class SceneSwapper(
    private val loadingScene: Scene,
    private val unload: (scene: Scene) -> Unit,
    private val load: (scene: Scene) -> Unit,
    private val activate: (scene: Scene) -> Unit,
): GameObject("Scene Swapper") {

    private var animation = ScummBlendAnimation()

    override val managedContent = mutableListOf<ManagedContent>(
        animation
    )

    private var state = SwapState.SWAP_DONE
    private var currentScene: Scene? = null
    private var nextScene: Scene? = null
    private var beforeNextScene: () -> Unit = { }

    fun swap(
        currentScene: Scene?,
        nextScene: Scene,
        beforeNextScene: () -> Unit = { }
    ) {
        info("swapping scene initiated: next '${nextScene.name}'")
        this.currentScene = currentScene
        this.nextScene = nextScene
        this.state = SwapState.SWAP_INIT
        this.beforeNextScene = beforeNextScene
    }

    override fun update(dt: Float) {
        animation.update(dt)

        when (state) {
            SwapState.SWAP_INIT -> {
                info("loading: ${loadingScene.name}")
                load(loadingScene)
                animation.playBlendOut()
                state = SwapState.SWAP_BLENDING_OUT
            }
            SwapState.SWAP_BLENDING_OUT -> {
                if (animation.hasFinished) {
                    info("activating: ${loadingScene.name}")
                    activate(loadingScene)
                    state = SwapState.SWAP_START_UNLOAD_OLD
                }
            }
            SwapState.SWAP_START_UNLOAD_OLD -> {
                if (currentScene != null) {
                    info("unloading: ${currentScene!!.name}")
                    unload(currentScene!!)
                }
                state = SwapState.SWAP_UNLOADING_OLD
            }
            SwapState.SWAP_UNLOADING_OLD -> {
                if (currentScene != null) {
                    state = SwapState.SWAP_START_LOAD_NEW
                    this.beforeNextScene()
                }
            }
            SwapState.SWAP_START_LOAD_NEW -> {
                info("loading: ${nextScene!!.name}")
                load(nextScene!!)
                state = SwapState.SWAP_LOADING_NEW
            }
            SwapState.SWAP_LOADING_NEW -> {
                animation.playBlendIn()
                state = SwapState.SWAP_BLENDING_IN
            }
            SwapState.SWAP_BLENDING_IN -> {
                info("activating: ${nextScene!!.name}")
                activate(nextScene!!)
                state = SwapState.SWAP_DONE
                info("swap done")
            }
            SwapState.SWAP_DONE -> {
                // nothing to do
            }
        }
    }

    fun render() {
        animation.render()
    }

}