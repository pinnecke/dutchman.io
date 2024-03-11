package com.mygdx.game.engine

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.mygdx.game.engine.memory.ManagedContent
import kotlin.math.abs

data class CameraDelta(
    var x: Float = 0f,
    var y: Float = 0f,
    var velocity: Float = 1f,
    var gravity: Float = 0f
)

class CameraPropMemory(
    private val camera: OrthographicCamera
): ManagedContent {
    override val contentIdentifier = "Camera delta store"

    private var lastPosition = Vector3()
    private var currentPosition = Vector3()
    private val buffer = Vector3()

    private var lastZoom = 1f
    private var currentZoom = 1f

    var delta = CameraDelta()

    override fun loadContent() {
        lastPosition.set(camera.position)
        currentPosition.set(camera.position)
        lastZoom = 1f
        currentZoom = 1f
    }

    override fun unloadContent() { }

    fun update() {
        handlePosition()
        handleZoom()
    }

    private fun handlePosition() {
        lastPosition.set(currentPosition)
        currentPosition.set(camera.position)

        buffer
            .set(currentPosition)
            .sub(lastPosition)

        delta.x = buffer.x
        delta.y = buffer.y
        delta.velocity = buffer.len()
    }

    private fun handleZoom() {
        lastZoom = currentZoom
        currentZoom = camera.zoom

        delta.gravity = (abs(currentZoom - lastZoom) * 100)
    }

}