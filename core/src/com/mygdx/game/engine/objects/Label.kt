package com.mygdx.game.engine.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.mygdx.game.engine.stdx.GameObject

class Label(
    val text: String,
    val textColor: Color,
    val x: Float,
    val y: Float
): GameObject {

    private var font: BitmapFont? = null
    
    override fun create() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/backissue_reg.otf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.genMipMaps = true
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
        parameter.magFilter = Texture.TextureFilter.Linear
        parameter.size = 28
        parameter.color = textColor
        font = generator.generateFont(parameter)
        generator.dispose()
    }

    override fun destroy() {
        font!!.dispose()
    }

    override fun render(batch: SpriteBatch) {
        font!!.draw(batch, text, x, y)
    }
}