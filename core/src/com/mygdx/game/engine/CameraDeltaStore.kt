package com.mygdx.game.engine

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.mygdx.game.engine.memory.ManagedContent

data class CameraDelta(
    var x: Float = 0f,
    var y: Float = 0f,
    var span: Float = 1f
)

class CameraDeltaStore(
    private val camera: OrthographicCamera
): ManagedContent {
    override val contentIdentifier = "Camera delta store"

    private var lastPosition = Vector3()
    private var currentPosition = Vector3()
    private val buffer = Vector3()

    var delta = CameraDelta()


    override fun loadContent() {
        lastPosition.set(camera.position)
        currentPosition.set(camera.position)
    }

    override fun unloadContent() { }

    fun update() {
        lastPosition.set(currentPosition)
        currentPosition.set(camera.position)

        buffer
            .set(currentPosition)
            .sub(lastPosition)

        delta.x = buffer.x
        delta.y = buffer.y
        delta.span = buffer.len()
    }

}