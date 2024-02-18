package com.mygdx.game.engine.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor

class InputProcessorTee: InputProcessor {

    private val inputProcessors = mutableListOf<InputProcessor>()

    fun add(inputProcessor: InputProcessor) {
        inputProcessors.add(inputProcessor)
    }

    fun clear() {
        inputProcessors.clear()
    }

    override fun keyDown(keycode: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.keyDown(keycode)) {
                return true
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.keyUp(keycode)) {
                return true
            }
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.keyTyped(character)) {
                return true
            }
        }
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchDown(screenX, screenY, pointer, button)) {
                return true
            }
        }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchUp(screenX, screenY, pointer, button)) {
                return true
            }
        }
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchCancelled(screenX, screenY, pointer, button)) {
                return true
            }
        }
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchDragged(screenX, screenY, pointer)) {
                return true
            }
        }
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.mouseMoved(screenX, screenY)) {
                return true
            }
        }
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.scrolled(amountX, amountY)) {
                return true
            }
        }
        return false
    }
}