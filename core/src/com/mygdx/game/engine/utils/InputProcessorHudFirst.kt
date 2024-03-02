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

    override fun touchDown(sceneX: Int, sceneY: Int, pointer: Int, button: Int): Boolean =
        if (!hudTranslator.touchDown(sceneX, sceneY, pointer, button)) {
            wordTranslator.touchDown(sceneX, sceneY, pointer, button)
        } else {
            true
        }

    override fun touchUp(sceneX: Int, sceneY: Int, pointer: Int, button: Int): Boolean =
        if (!hudTranslator.touchUp(sceneX, sceneY, pointer, button)) {
            wordTranslator.touchUp(sceneX, sceneY, pointer, button)
        } else {
            true
        }

    override fun touchCancelled(sceneX: Int, sceneY: Int, pointer: Int, button: Int): Boolean =
        if (!hudTranslator.touchCancelled(sceneX, sceneY, pointer, button)) {
            wordTranslator.touchCancelled(sceneX, sceneY, pointer, button)
        } else {
            true
        }

    override fun touchDragged(sceneX: Int, sceneY: Int, pointer: Int): Boolean =
        if (!hudTranslator.touchDragged(sceneX, sceneY, pointer)) {
            wordTranslator.touchDragged(sceneX, sceneY, pointer)
        } else {
            true
        }

    override fun mouseMoved(sceneX: Int, sceneY: Int): Boolean =
        if (!hudTranslator.mouseMoved(sceneX, sceneY)) {
            wordTranslator.mouseMoved(sceneX, sceneY)
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