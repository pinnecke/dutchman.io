package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.engine.memory.ManagedContent

class RenderBuffer(
    override val id: String,
    val width: Int = (Engine.canvas.surface.width).toInt(),
    val height: Int = (Engine.canvas.surface.height).toInt()
): ManagedContent {
    private var worldFboCamera: OrthographicCamera? = null
    private var worldFboViewport: Viewport? = null
    private var frameBuffer: FrameBuffer? = null
    private var sprite: Sprite? = null
    private var batch: SpriteBatch? = null

    override fun loadContent() {
        worldFboCamera = OrthographicCamera()
        worldFboViewport = ExtendViewport(1600f, 1050f, 1920f, 1200f, worldFboCamera)
        worldFboViewport!!.update(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, true)

        worldFboCamera!!.translate(25f, 75f ,0f)
        worldFboCamera!!.update()

        frameBuffer = FrameBuffer(Pixmap.Format.RGB565, width, height, false)
        sprite = Sprite(frameBuffer!!.colorBufferTexture)
        sprite!!.flip(false, true)

        batch = SpriteBatch()
    }

    override fun unloadContent() {
        frameBuffer!!.dispose()
        batch!!.dispose()
    }

    fun resize(windowWidth: Int, windowHeight: Int) {
        worldFboViewport!!.update(windowWidth, windowHeight)
    }

    fun renderIntoBuffer(action: () -> Unit) {
        frameBuffer!!.begin()
        action()
        frameBuffer!!.end()
    }

    fun renderOntoScreen(action: (screen: SpriteBatch, content: Sprite) -> Unit) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        with (batch!!) {
            begin()
            enableBlending()
            worldFboViewport!!.apply()
            projectionMatrix = worldFboViewport!!.camera.combined
            action(this, sprite!!)
            end()
        }
    }

}