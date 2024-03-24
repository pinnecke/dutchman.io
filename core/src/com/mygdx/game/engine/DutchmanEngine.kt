package com.mygdx.game.engine

import com.badlogic.gdx.Gdx
import kotlin.reflect.KClass

abstract class DutchmanGame(val sceneManager: SceneManager) {
    abstract val scenes: List<Scene>
}

class DutchmanEngine {
    private var sceneManager: SceneManager? = null

    fun create(namespace: String, bootScene: KClass<*>, init: (sceneManager: SceneManager) -> DutchmanGame) {
        sceneManager = SceneManager(namespace, bootScene)
        with (sceneManager!!) {
            val game = init(this)
            register(game.scenes)
            loadContent()
        }
    }

    fun render() = with(sceneManager!!) {
        update(Gdx.graphics.deltaTime)
        render()
    }

    fun resize(width: Int, height: Int) =
        sceneManager!!.resize(width, height)

    fun dispose() {
        sceneManager!!.unloadContent()
    }


}