package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.mygdx.game.engine.Config
import com.mygdx.game.engine.Engine
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.objects.Rectangle
import com.mygdx.game.engine.stdx.GameObject

class DiagnosticsPanel: GameObject("Diagnostic Panel") {

    private var font: BitmapFont? = null

    private var rect = Rectangle(
        x = 200f,
        y = 125f,
        width = Engine.canvas.surface.width - 405f,
        height = 3f,
        color = Engine.colors.dimmedGray
    )

    private var rectProgress = Rectangle(
        x = rect.x,
        y = rect.y,
        width = 0f,
        height = rect.height,
        color = Engine.colors.dimmedRed
    )

    override val managedContent = mutableListOf(
        managedContentOf(
            "Font",
            load = {
                val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/ubuntu-mono-reg.ttf"))
                val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
                parameter.genMipMaps = true
                parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
                parameter.magFilter = Texture.TextureFilter.Linear
                parameter.size = 20
                parameter.color = Color.WHITE
                parameter.borderColor = Engine.colors.lightGray
                parameter.borderWidth = 1f
                font = generator.generateFont(parameter)
                generator.dispose()
            },
            unload = {
                font!!.dispose()
            }
        ),
        rect,
        rectProgress
    )

    var visible: Boolean = true
    var enabled: Boolean = Config.DEBUG_SHOW_DIAGNOSTICS

    var sceneName: String = "Unknown"
    var shotName: String = "Unknown"
    var shotTotal: Float = Float.POSITIVE_INFINITY
    var shotElapsed: Float = 0f
    var shotProgress: Float = 0f





    override fun update(dt: Float) {
        if (enabled) {
            rect.update(dt)
            rectProgress.update(dt)
        }
    }

    override fun render(batch: SpriteBatch) {
        if (enabled) {
            if (visible) {
                Gdx.gl.glEnable(GL20.GL_BLEND)
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

                rect.render(batch)

                if (shotTotal.isFinite() && shotElapsed.isFinite()) {
                    rectProgress.width = shotProgress * rect.width
                    rectProgress.render(batch)

                    font!!.draw(batch, "$sceneName : $shotName", 210f, rect.y - 18f)

                    val totalMin = "%.0f".format(shotTotal / 60f).padStart(2, '0')
                    val totalSec = "%.0f".format(shotTotal % 60f).padStart(2, '0')
                    val elapsedMin = "%.0f".format(shotElapsed / 60f).padStart(2, '0')
                    val elapsedSec = "%.0f".format(shotElapsed % 60f).padStart(2, '0')
                    font!!.draw(batch, "$elapsedMin:$elapsedSec / $totalMin:$totalSec", rect.width - 10f, rect.y - 18f)
                }
            }
        }
    }
}