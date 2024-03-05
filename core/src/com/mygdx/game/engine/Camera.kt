package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.stdx.GameObject

class GameCamera(
): GameObject {
    var camera: OrthographicCamera? = null

    override fun create() {

    }

    override fun destroy() {

    }

    fun move(shot: CameraShot) {
        camera!!.position.x = shot.center.x
        camera!!.position.y = shot.center.y
        camera!!.zoom = shot.zoom
    }

}

class CameraShot(
    val shot: Surface2D
): GameObject {

    val center = shot.center
    val zoom = shot.width / Engine.canvas.surface.width

    private val debugRenderer = DebugRenderer(
        Config.DEBUG_RENDER_SHOW_CAMERA_SHOTS, Color.ORANGE
    )

    override fun create() {
        debugRenderer.create()
    }

    override fun destroy() {
        debugRenderer.destroy()
    }

    override fun render(batch: SpriteBatch) {
        debugRenderer.render(batch) {
            // the actual safe-zone bounds
            val szWidth = zoom * (Engine.canvas.safeZone.width - Engine.canvas.safeZone.left)
            val szHeight = zoom * (Engine.canvas.safeZone.height - Engine.canvas.safeZone.bottom)
            val szDw = shot.width - szWidth
            val szDh = shot.height - szHeight

            // render parts of the view that might not be in the perspective of the camera
            it.renderColor = Engine.colors.dimmedGray
            it.filled(
                batch.projectionMatrix,
                shot.left, shot.bottom,
                szDw / 2f, shot.height
            )
            it.filled(
                batch.projectionMatrix,
                shot.left + szWidth + szDw / 2f, shot.bottom,
                szDw / 2f, shot.height
            )
            it.filled(
                batch.projectionMatrix,
                shot.left + szDw / 2, shot.bottom,
                shot.width - szDw, szDh / 2f
            )
            it.filled(
                batch.projectionMatrix,
                shot.left + szDw / 2, shot.bottom + szHeight + szDh / 2f,
                shot.width - szDw, szDh / 2f
            )

            // render lines for actual bounds of shot
            it.renderColor = Color.ORANGE
            it.rect(
                batch.projectionMatrix,
                shot.left, shot.bottom,
                shot.width, shot.height,
                lineWidth = 2
            )
/*
            // render lines for safe-zone bounds
            it.renderColor = Color.ORANGE
            it.rect(
                batch.projectionMatrix,
                shot.left + (szDw / 2f), shot.bottom + (szDh / 2f),
                szWidth,
                szHeight,
                lineWidth = 2
            )*/
        }
    }

}