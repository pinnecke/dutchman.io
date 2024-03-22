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
    var text: String = "",
    var textColor: Color = Color.WHITE,
    var x: Float = 0f,
    var y: Float = 0f,
    val fontSize: Int = 28,
    var visible: Boolean = true,
    var borderColor: Color = Color.BLACK,
    var borderWidth: Float = 0f
): GameObject("Label - ${text.replace("\n", ", ")}") {

    private var font: BitmapFont? = null
    private val resetVisible = visible

    override val managedContent = mutableListOf(
        managedContentOf(
            id = "Font",
            load = {
                visible = resetVisible

                val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/backissue_reg.otf"))
                val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
                parameter.genMipMaps = true
                parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest
                parameter.magFilter = Texture.TextureFilter.Linear
                parameter.size = fontSize
                parameter.color = textColor
                parameter.borderColor = borderColor
                parameter.borderWidth = borderWidth
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