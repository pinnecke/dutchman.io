package com.mygdx.game.engine.utils

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

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchDown(x, y, pointer, button)) {
                return true
            }
        }
        return false
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchUp(x, y, pointer, button)) {
                return true
            }
        }
        return false
    }

    override fun touchCancelled(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchCancelled(x, y, pointer, button)) {
                return true
            }
        }
        return false
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.touchDragged(x, y, pointer)) {
                return true
            }
        }
        return false
    }

    override fun mouseMoved(x: Int, y: Int): Boolean {
        for (inputProcessor in inputProcessors) {
            if (inputProcessor.mouseMoved(x, y)) {
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