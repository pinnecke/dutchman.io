package com.mygdx.game.engine.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.stdx.GameObject

class Label(
    var text: String,
    val textColor: Color,
    val x: Float,
    val y: Float,
    val fontSize: Int = 28
): GameObject("Label - ${text.replace("\n", ", ")}") {

    private var font: BitmapFont? = null
    var visible: Boolean = true

    override val managedContent = mutableListOf(
        managedContentOf(
            contentIdentifier = "Font",
            load = {
                visible = true
                val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/backissue_reg.otf"))
                val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
                parameter.genMipMaps = true
                parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
                parameter.magFilter = Texture.TextureFilter.Linear
                parameter.size = fontSize
                parameter.color = textColor
                font = generator.generateFont(parameter)
                generator.dispose()
            },
            unload = {
                font!!.dispose()
            }
        )
    )


    override fun render(batch: SpriteBatch) {
        if (visible) {
            font!!.draw(batch, text, x, y)
        }
    }
}