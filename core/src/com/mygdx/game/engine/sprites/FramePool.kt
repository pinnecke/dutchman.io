package com.mygdx.game.engine.sprites

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.mygdx.game.engine.stdx.Pool
import com.mygdx.game.engine.stdx.Rc

typealias FilePath = String
typealias Frame = Rc<Texture, FilePath>
typealias FramePool = Pool<Texture, FilePath, Frame>

fun framePool(
    loadTexture: (filePath: String) -> Texture = {
        val texture = Texture(Gdx.files.internal(it), true)
        texture.setFilter(
            TextureFilter.MipMapLinearNearest,
            TextureFilter.Linear
        )
        texture
    }
) = FramePool(
    fetch = loadTexture,
    construct = { filePath, texture, free ->
        Frame(filePath, texture, free)
    },
    free = { it.value.dispose() }
)