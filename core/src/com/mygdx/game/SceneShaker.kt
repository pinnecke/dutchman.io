package com.mygdx.game

import com.badlogic.gdx.graphics.OrthographicCamera
import com.mygdx.game.engine.ShakeAnimation
import com.mygdx.game.engine.memory.ManagedContent
import com.mygdx.game.engine.stdx.Update

class SceneShaker(
    private var getCamera: () -> OrthographicCamera,
    private var settings: ShakeAnimation.Settings
): ManagedContent, Update {

    override val id: String = "scene shaker"
    private var camera: OrthographicCamera? = null

    val effect = ShakeAnimation(
        name = "scene shaker",
        settings = settings
    )

    var speed: Float
        get() { return effect.speed }
        set(value) { effect.speed = value }

    var amount: Float
        get() { return effect.amount }
        set(value) { effect.amount = value }

    override fun loadContent() {
        effect.loadContent()
        camera = getCamera()
    }

    override fun unloadContent() {
        effect.unloadContent()
    }

    override fun update(dt: Float) {
        effect.update(dt)
        camera!!.position.x += effect.horizontal
        camera!!.position.y += effect.vertical
    }

    fun reset() {
        amount = settings.amount
        speed = settings.amount
        effect.vertical = settings.verticalAmount
        effect.horizontal = settings.horizontalAmount
    }

}