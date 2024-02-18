package com.mygdx.game.engine.utils

import com.badlogic.gdx.InputProcessor

class InputProcessorHudFirst(
    private val hudTranslator: InputProcessorTranslator,
    private val wordTranslator: InputProcessorTranslator
): InputProcessor {

    override fun keyDown(keycode: Int): Boolean =
        if (!hudTranslator.keyDown(keycode)) {
            wordTranslator.keyDown(keycode)
        } else {
            true
        }

    override fun keyUp(keycode: Int): Boolean =
        if (!hudTranslator.keyUp(keycode)) {
            wordTranslator.keyUp(keycode)
        } else {
            true
        }

    override fun keyTyped(character: Char): Boolean =
        if (!hudTranslator.keyTyped(character)) {
            wordTranslator.keyTyped(character)
        } else {
            true
        }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        if (!hudTranslator.touchDown(screenX, screenY, pointer, button)) {
            wordTranslator.touchDown(screenX, screenY, pointer, button)
        } else {
            true
        }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        if (!hudTranslator.touchUp(screenX, screenY, pointer, button)) {
            wordTranslator.touchUp(screenX, screenY, pointer, button)
        } else {
            true
        }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean =
        if (!hudTranslator.touchCancelled(screenX, screenY, pointer, button)) {
            wordTranslator.touchCancelled(screenX, screenY, pointer, button)
        } else {
            true
        }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean =
        if (!hudTranslator.touchDragged(screenX, screenY, pointer)) {
            wordTranslator.touchDragged(screenX, screenY, pointer)
        } else {
            true
        }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean =
        if (!hudTranslator.mouseMoved(screenX, screenY)) {
            wordTranslator.mouseMoved(screenX, screenY)
        } else {
            true
        }

    override fun scrolled(amountX: Float, amountY: Float): Boolean =
        if (!hudTranslator.scrolled(amountX, amountY)) {
            wordTranslator.scrolled(amountX, amountY)
        } else {
            true
        }
}