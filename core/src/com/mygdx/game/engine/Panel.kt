package com.mygdx.game.engine

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.stdx.GameObject

class Panel(
    val surface: Surface2D,
    caption: String = "Untitled"
): GameObject {

    val center = surface.center
    val zoom = surface.width / Engine.canvas.surface.width

    var caption: String
        get() { return label.text }
        set(value) { label.text = value }

    private val label = Label(
        text = caption,
        textColor = Color.WHITE,
        x = surface.left,
        y = surface.top + 33f,
        fontSize = 20
    )

    private val debugRenderer = DebugRenderer(
        Config.DEBUG_RENDER_SHOW_CAMERA_SHOTS, Color.ORANGE
    )

    override fun create() {
        debugRenderer.create()
        label.create()
    }

    override fun destroy() {
        debugRenderer.destroy()
        label.destroy()
    }

    override fun render(batch: SpriteBatch) {
        debugRenderer.render(batch) {
            // the actual safe-zone bounds
            val szWidth = zoom * (Engine.canvas.safeZone.width - Engine.canvas.safeZone.left)
            val szHeight = zoom * (Engine.canvas.safeZone.height - Engine.canvas.safeZone.bottom)
            val szDw = surface.width - szWidth
            val szDh = surface.height - szHeight

            // render parts of the view that might not be in the perspective of the camera
            it.renderColor = Engine.colors.dimmedGray
            it.filled(
                batch.projectionMatrix,
                surface.left, surface.bottom,
                szDw / 2f, surface.height
            )
            it.filled(
                batch.projectionMatrix,
                surface.left + szWidth + szDw / 2f, surface.bottom,
                szDw / 2f, surface.height
            )
            it.filled(
                batch.projectionMatrix,
                surface.left + szDw / 2, surface.bottom,
                surface.width - szDw, szDh / 2f
            )
            it.filled(
                batch.projectionMatrix,
                surface.left + szDw / 2, surface.bottom + szHeight + szDh / 2f,
                surface.width - szDw, szDh / 2f
            )

            // render the panel developer bounds
            it.renderColor = Color.BLACK
            it.filled(
                batch.projectionMatrix,
                surface.left, surface.top,
                surface.width, 50f
            )
            it.filled(
                batch.projectionMatrix,
                surface.left, surface.bottom - 20f,
                surface.width, 20f
            )
            it.filled(
                batch.projectionMatrix,
                surface.left - 20, surface.bottom - 20f,
                20f, surface.height + 70f
            )
            it.filled(
                batch.projectionMatrix,
                surface.right, surface.bottom - 20f,
                20f, surface.height + 70f
            )

            // render the panel name
            batch.begin()
            label.render(batch)
            batch.end()

            // render lines for actual bounds of shot
            it.renderColor = Color.ORANGE
            it.rect(
                batch.projectionMatrix,
                surface.left, surface.bottom,
                surface.width, surface.height,
                lineWidth = 2
            )


        }
    }

}