package com.mygdx.game.engine.utils

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2

class InputProcessorTranslator(
    private val unproject: (x: Float, y: Float) -> Vector2,
    private val forward: InputProcessor
): InputAdapter() {

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val local = unproject(screenX.toFloat(), screenY.toFloat())
        return forward.touchDown(local.x.toInt(), local.y.toInt(), pointer, button)
    }

}