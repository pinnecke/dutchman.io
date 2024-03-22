package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.utils.viewport.Viewport
import com.crashinvaders.vfx.VfxManager
import com.crashinvaders.vfx.effects.*
import com.crashinvaders.vfx.effects.util.GammaThresholdEffect
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.Update

class ScenePostEffects(
    private val getViewport: () -> Viewport
): ManagedContent, Update {

    override val id = "scene pos processing effects"
    private var manager: VfxManager? = null
    private var viewport: Viewport? = null

    fun reset() {
        blur.enabled = false
        gamma.enabled = false
        crt.enabled = false
        grain.enabled = false
        white.enabled = false
        vignette.enabled = false
    }

    val blur = Tween(
        id = "blur effect",
        create = { GaussianBlurEffect() },
        enable = { manager?.addEffect(it) },
        disable = { manager?.removeEffect(it) },
        destroy = { it.dispose() },
        configure = { effect, amount -> effect.amount = amount }
    )

    val gamma = Tween(
        id = "gamma effect",
        create = { GammaThresholdEffect(GammaThresholdEffect.Type.RGBA) },
        enable = { manager?.addEffect(it) },
        disable = { manager?.removeEffect(it) },
        destroy = { it.dispose() },
        configure = { effect, amount ->
            effect.gamma = amount
        }
    )

    val crt = Tween(
        id = "crt effect",
        create = { CrtEffect(CrtEffect.LineStyle.HORIZONTAL_SMOOTH, 0.85f, 1.0f) },
        enable = { manager?.addEffect(it) },
        disable = { manager?.removeEffect(it) },
        destroy = { it.dispose() },
        configure = { _, _ -> }
    )

    val grain = Tween(
        id = "grain effect",
        create = { FilmGrainEffect() },
        enable = { manager?.addEffect(it) },
        disable = { manager?.removeEffect(it) },
        destroy = { it.dispose() },
        configure = { effect, amount ->
            effect.noiseAmount = amount
        }
    )

    val white = Tween(
        id = "white blend effect",
        create = { LevelsEffect() },
        enable = { manager?.addEffect(it) },
        disable = { manager?.removeEffect(it) },
        destroy = { it.dispose() },
        configure = { effect, amount ->
            effect.brightness = amount
        }
    )

    val vignette = Tween(
        id = "vignetting effect",
        create = { VignettingEffect(false) },
        enable = { manager?.addEffect(it) },
        disable = { manager?.removeEffect(it) },
        destroy = { it.dispose() },
        configure = { effect, amount ->
            effect.intensity = amount
        }
    )

    override fun loadContent() {
        viewport = getViewport()
        manager = VfxManager(Pixmap.Format.RGBA8888)
        blur.loadContent()
        gamma.loadContent()
        crt.loadContent()
        grain.loadContent()
        white.loadContent()
        vignette.loadContent()
    }

    override fun unloadContent() {
        manager?.dispose()
        blur.unloadContent()
        gamma.unloadContent()
        crt.unloadContent()
        grain.unloadContent()
        white.unloadContent()
        vignette.unloadContent()
    }

    override fun update(dt: Float) {
        blur.update(dt)
        gamma.update(dt)
        crt.update(dt)
        grain.update(dt)
        white.update(dt)
        vignette.update(dt)
    }

    fun resize() {
        manager?.resize(2 * viewport!!.screenWidth, 2 * viewport!!.screenHeight)
    }

    fun apply(renderAction: () -> Unit) {
        resize()
        manager?.cleanUpBuffers()
        manager?.beginInputCapture()
        renderAction()
        manager?.endInputCapture()
        manager?.applyEffects()
        manager?.renderToScreen(0, 0, 2 * viewport!!.screenWidth, 2 * viewport!!.screenHeight)

    }

}