package com.mygdx.game.engine

import com.badlogic.gdx.Application.LOG_INFO
import com.badlogic.gdx.Gdx
import com.mygdx.game.engine.stdx.DynamicComponent
import com.mygdx.game.engine.utils.info

class Allocator: DynamicComponent {

    override val componentName: String
        get() = this::class.simpleName!!

    private val resources = mutableSetOf<DynamicComponent>()

    fun register(component: DynamicComponent) {
        resources.add(component)
    }

    override fun create() {
        resources.forEach {
            info("creating: ${it.componentName}")
            it.create()
        }
    }

    override fun destroy() {
        resources.forEach {
            info("destroying: ${it.componentName}")
            it.destroy()
        }
    }
}