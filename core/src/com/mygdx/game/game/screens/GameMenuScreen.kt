package com.mygdx.game.game.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Polygon
import com.mygdx.game.engine.*
import com.mygdx.game.engine.hotspots.HudButton

class GameMenuScreen: Screen() {

    private var img: Texture? = null
    private var hotspot = HudButton(
        160f,
        75f,
        closedPolygon(
            0f, 0f,
            0f, 80f,
            1600f, 1050f,
            50f, 0f
        ),
        this,
        EventFilter.builder()
            .touchDown { _, _ ->
                println("Clicked")
                switch(SplashScreen::class)
            }
                .any()
                .build()
            .build()
    )

    override fun update(dt: Float) {
        hotspot.update(dt)
    }

    override fun loadContents() = object: StepAction {
        override var hasFinished: Boolean = false

        override fun start() {
            img = Texture("world.png")
            hotspot.loadContent()
            hasFinished = true
        }

        override fun step() {

        }

    }

    override fun unloadContents() = object: StepAction {
        override var hasFinished: Boolean = false

        override fun start() {
            img!!.dispose()

            hotspot.unloadContent()
            hasFinished = true
        }

        override fun step() {
        }

    }

    override fun renderWorld(batch: SpriteBatch) {
        batch.draw(img, 0f, 0f)
        hotspot.render(LayerType.WORLD, batch)
    }

    override fun renderHud(batch: SpriteBatch) {
        batch!!.draw(img, super.width - 45f, super.height - 45f)
        hotspot.render(LayerType.HUD, batch)
    }


}