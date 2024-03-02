package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import kotlin.reflect.KClass

interface DutchmanGame {
    val namespace: String
    val splash: KClass<*>
    val scenes: List<Scene>
}

class DutchmanEngine {
    private var sceneManager: SceneManager? = null

    fun create(game: DutchmanGame) {
        sceneManager = SceneManager(game.namespace)
        with (sceneManager!!) {
            register(game.scenes)
            startup(game.splash)
        }
    }

    fun render() = with(sceneManager!!) {
        update(Gdx.graphics.deltaTime)
        render()
    }

    fun resize(width: Int, height: Int) =
        sceneManager!!.resize(width, height)

    fun dispose() =
        sceneManager!!.shutdown()

}