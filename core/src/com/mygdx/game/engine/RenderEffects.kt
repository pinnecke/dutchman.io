package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.mygdx.game.engine.memory.ManagedContent

class ShaderCompilationError(
    shaderName: String,
    reasons: String
): RuntimeException(
    "Shader '$shaderName' was not compiled:\n$reasons"
)

open class ShaderEffect(
    override val id: String,
    private val vertexShaderFile: String = "shader/default.vert.glsl",
    private val fragmentShaderFile: String = "shader/default.frag.glsl",
    private val readContents: (filePath: String) -> String = {
        Gdx.files.internal(it).readString()
    }
): ManagedContent {

    protected var program: ShaderProgram? = null

    override fun loadContent() {
        ShaderProgram.pedantic = true
        program = ShaderProgram(readContents(vertexShaderFile), readContents(fragmentShaderFile))
        if (!program!!.isCompiled) {
            throw ShaderCompilationError(id, program!!.log)
        }
    }

    override fun unloadContent() {
        program!!.dispose()
    }

    fun render(batch: SpriteBatch, action: (shaderBatch: SpriteBatch) -> Unit) {
        val former = batch.shader
        batch.shader = program
        action(batch)
        batch.shader = former
    }
}

class BlackAndWhiteRenderEffect: ShaderEffect(
    id = "black&white effect",
    fragmentShaderFile = "shader/blackandwhite.frag.glsl"
) {
    var amount: Float = 0.0f
        set(value) {
            program!!.bind()
            program!!.setUniformf("u_amount", value)
            if (value in 0f .. 1f) {
                field = value
            }
        }
}

class RenderEffects {
    companion object {
        fun blackAndWhiteShaderEffect() = BlackAndWhiteRenderEffect()
        fun outlineShaderEffect() = ShaderEffect(
            id = "outline effect",
            vertexShaderFile = "shader/outline.vert.glsl",
            fragmentShaderFile = "shader/outline.frag.glsl",

        )
    }
}

