package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject
import com.mygdx.game.engine.stdx.runRepeated

enum class Appearance {
    DEFAULT,
    BLEND_OUT,
    BLEND_IN,
    HOTSPOT_IN,
    HOTSPOT_OUT,
    HIDDEN
}

class GameCursorApi(
    private val cursor: GameCursor
) {
    var visible: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                if (value && cursor.appearance != Appearance.BLEND_IN) {
                    cursor.appearance = Appearance.BLEND_IN
                } else if (!value && cursor.appearance != Appearance.BLEND_OUT) {
                    hotspot = false
                    cursor.appearance = Appearance.BLEND_OUT
                }
            }
        }
    var hotspot: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                if (visible) {
                    if (value) {
                        cursor.appearance = Appearance.HOTSPOT_IN
                    } else {
                        cursor.appearance = Appearance.HOTSPOT_OUT
                    }
                }
            }
        }
}

class GameCursor(
    fps: Int = 60
): GameObject(
    contentIdentifier = "cursor"
) {
    private var cursorLast: Cursor? = null
    private var cursorNone: Cursor? = null
    private var cursorDefault: Cursor? = null
    private val cursorIn: MutableList<Cursor> = mutableListOf()
    private val cursorOut: MutableList<Cursor> = mutableListOf()
    private val cursorHot: MutableList<Cursor> = mutableListOf()
    private var index: Int = 0
    private var animationDone: Boolean = true

    var appearance: Appearance = Appearance.BLEND_OUT
        set(value) {
            index = 0
            animationDone = false
            field = value
        }

    override val managedContent = mutableListOf(
        managedContentOf(
            id = "cursor images",
            load = {
                val filePath = { type: String, i: Int ->
                    "cursors/cursor-${type}-${i.toString().padStart(2, '0')}.png"
                }
                cursorNone = createCursor(filePath("none", 0))
                cursorDefault = createCursor(filePath("default", 0))
                cursorLast = cursorDefault
                Gdx.graphics.setCursor(cursorNone)

                (0..13).map { filePath("in", it) }.forEach { cursorIn.add(createCursor(it)) }
                (0..8).map { filePath("out", it) }.forEach { cursorOut.add(createCursor(it)) }
                (0..13).map { filePath("hot", it) }.forEach { cursorHot.add(createCursor(it)) }
            },
            unload = {
                (cursorIn + cursorOut + cursorHot).forEach {
                    it.dispose()
                }
                cursorDefault!!.dispose()
                cursorNone!!.dispose()
            }
        )
    )

    companion object {
        const val X_HOTSPOT = 15
        const val Y_HOTSPOT = 15
    }

    override fun update(dt: Float) {
        nextFrame.update(dt)
    }

    override fun render(batch: SpriteBatch) {
        super.render(batch)
    }

    private val nextFrame = runRepeated(1/fps.toFloat()) {
        if (!animationDone) {
            updateCursorImage()
        }
    }

    private fun cursorImage(frames: List<Cursor>, reverse: Boolean = false): Cursor {
        animationDone = animationDone || (index + 1 == frames.size)
        index = if (animationDone) { index } else { index + 1 }
        return frames[if (reverse) { frames.size - index } else { index }]
    }

    private fun updateCursorImage() {
        cursorLast = when (appearance) {
            Appearance.DEFAULT -> cursorDefault!!
            Appearance.BLEND_OUT -> cursorImage(cursorOut)
            Appearance.BLEND_IN -> cursorImage(cursorIn)
            Appearance.HOTSPOT_IN -> cursorImage(cursorHot)
            Appearance.HOTSPOT_OUT -> cursorImage(cursorHot, reverse = true)
            Appearance.HIDDEN -> cursorNone!!
        }
        Gdx.graphics.setCursor(cursorLast)
    }

    private fun createCursor(texturePath: String): Cursor {
        val pixmap = Pixmap(Gdx.files.internal(texturePath))
        val cursor = Gdx.graphics.newCursor(pixmap, X_HOTSPOT, Y_HOTSPOT)
        pixmap.dispose()
        return cursor
    }

}