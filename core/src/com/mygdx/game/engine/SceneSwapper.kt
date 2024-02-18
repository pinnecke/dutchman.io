package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.mygdx.game.game.screens.LoadingScreen

enum class SwapState {
    SWAP_INIT,
    SWAP_BLENDING_OUT,
    SWAP_START_UNLOAD_OLD,
    SWAP_UNLOADING_OLD,
    SWAP_START_LOAD_NEW,
    SWAP_LOADING_NEW,
    SWAP_BLENDING_IN,
    SWAP_SHOW_NEW_SCREEN,
    SWAP_DONE
}

enum class AnimationState {
    BLEND_OUT,
    BLEND_IN
}

class SwappingAnimation {

    var hasFinished: Boolean = true

    private val duration = 0.5f
    private var state = AnimationState.BLEND_OUT
    private var elapsed = 0f

    private var shapeRenderer: ShapeRenderer? = null

    fun loadContent() {
        shapeRenderer = ShapeRenderer()
        shapeRenderer!!.color = Color.BLACK
    }

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

    fun update(dt: Float) {
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

    fun render(projectionMatrix: Matrix4, windowWidth: Float, windowHeight: Float) {
        if (!hasFinished) {

            val alpha = elapsed / duration
            shapeRenderer!!.projectionMatrix = projectionMatrix
            shapeRenderer!!.begin(ShapeRenderer.ShapeType.Filled)

            when (state) {
                AnimationState.BLEND_OUT -> {
                    val horizontal = alpha / 2f * windowWidth
                    val vertical = alpha / 2f * windowHeight

                    shapeRenderer!!.rect(
                        0f,
                        0f,
                        horizontal,
                        windowHeight
                    )
                    shapeRenderer!!.rect(
                        windowWidth - horizontal,
                        0f,
                        horizontal,
                        windowHeight
                    )
                    shapeRenderer!!.rect(
                        0f,
                        0f,
                        windowWidth,
                        vertical
                    )
                    shapeRenderer!!.rect(
                        0f,
                        windowHeight - vertical,
                        windowWidth,
                        vertical
                    )
                }
                AnimationState.BLEND_IN -> {

                    shapeRenderer!!.rect(
                        alpha / 2f * windowWidth,
                        alpha / 2f * windowHeight,
                        windowWidth - (alpha * windowWidth),
                        windowHeight - (alpha * windowHeight)
                    )
                }
            }

            shapeRenderer!!.end()
        }
    }
}

class SceneSwapper(
    private val fetchLoadingScreen: () -> LoadingScreen,
    private val unload: (screen: Screen) -> Unit,
    private val load: (screen: Screen) -> Unit,
    private val activate: (screen: Screen) -> Unit
) {
    private var state = SwapState.SWAP_DONE
    private var animation = SwappingAnimation()
    private var loadingScreen: Screen? = null
    private var currentScreen: Screen? = null
    private var nextScreen: Screen? = null

    fun loadContent() {
        animation.loadContent()
    }

    fun swap(
        currentScreen: Screen?,
        nextScreen: Screen
    ) {
        this.currentScreen = currentScreen
        this.nextScreen = nextScreen
        this.state = SwapState.SWAP_INIT
    }

    fun update(dt: Float) {
        animation.update(dt)

        when (state) {
            SwapState.SWAP_INIT -> {
                if (loadingScreen == null) {
                    loadingScreen = fetchLoadingScreen()
                    load(loadingScreen!!)
                }
                animation.playBlendOut()
                state = SwapState.SWAP_BLENDING_OUT
            }
            SwapState.SWAP_BLENDING_OUT -> {
                if (animation.hasFinished) {
                    activate(loadingScreen!!)
                    state = SwapState.SWAP_START_UNLOAD_OLD
                }
            }
            SwapState.SWAP_START_UNLOAD_OLD -> {
                if (currentScreen != null) {
                    unload(currentScreen!!)
                }
                state = SwapState.SWAP_UNLOADING_OLD
            }
            SwapState.SWAP_UNLOADING_OLD -> {
                if (currentScreen != null && currentScreen!!.hasUnloaded) {
                    state = SwapState.SWAP_START_LOAD_NEW
                }
            }
            SwapState.SWAP_START_LOAD_NEW -> {
                load(nextScreen!!)
                state = SwapState.SWAP_LOADING_NEW
            }
            SwapState.SWAP_LOADING_NEW -> {
                if (nextScreen!!.hasLoaded) {
                    animation.playBlendIn()
                    state = SwapState.SWAP_BLENDING_IN
                }
            }
            SwapState.SWAP_BLENDING_IN -> {
                if (animation.hasFinished) {
                    state = SwapState.SWAP_SHOW_NEW_SCREEN
                }
            }
            SwapState.SWAP_SHOW_NEW_SCREEN -> {
                activate(nextScreen!!)
                state = SwapState.SWAP_DONE
            }
            SwapState.SWAP_DONE -> {
                // nothing to do
            }
        }
    }

    fun render(projectionMatrix: Matrix4, windowWidth: Float, windowHeight: Float) {
        animation.render(projectionMatrix, windowWidth, windowHeight)
    }

}