package com.mygdx.game.engine

import com.mygdx.game.engine.stdx.DynamicComponent

class ComponentManager: DynamicComponent {
    private val resources = mutableSetOf<DynamicComponent>()

    fun register(component: DynamicComponent) {
        resources.add(component)
    }

    override fun create() {
        resources.forEach {
            println("creating $it...")
            it.create()
        }
    }

    override fun destroy() {
        resources.forEach {
            println("destroying $it...")
            it.destroy()
        }
    }
}