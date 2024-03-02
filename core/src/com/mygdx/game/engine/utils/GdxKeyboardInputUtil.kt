package com.mygdx.game.engine.utils

import com.badlogic.gdx.Gdx

class GdxKeyboardInputUtil {
    private val mappings = mutableMapOf<Int, () -> Unit>()

    operator fun set(value: Int, action: () -> Unit) {
        mappings[value] = action
    }

    fun act() {
        mappings.keys.forEach {
            if (Gdx.input.isKeyPressed(it))  {
                mappings[it]?.let { action -> action() }
            }
        }
    }
}