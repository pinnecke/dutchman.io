package com.mygdx.game.game.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Polygon
import com.mygdx.game.engine.*
import com.mygdx.game.engine.hotspots.HudButton
import com.mygdx.game.engine.sprites.FrameAnimation
import com.mygdx.game.engine.stdx.once
import com.mygdx.game.engine.stdx.runDelayed
import com.mygdx.game.engine.stdx.runTriggered
import com.mygdx.game.engine.stdx.seconds

class GameMenuScreen: Screen() {

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
                switch(GameMenuScreen::class)
            }
                .any()
                .build()
            .build()
    )

    private var batman = FrameAnimation(
        name = "batman",
        sheets = super.sheets
    )

    private var enterCinematic = runDelayed(
        delay = 2.seconds()) {
        enterCinematicMode {
            println("Cinematic Mode entered!")
            exitCinematic.start()
        }
    }

    private var exitCinematic = runTriggered(
        delay = 5.seconds()) {
        exitCinematicMode {
            println("Cinematic Mode exited!")
        }
    }

    override fun update(dt: Float) {
        hotspot.update(dt)
        enterCinematic.update(dt)
        exitCinematic.update(dt)
        batman.update(dt)
    }

    override fun loadContents() = object: StepAction {
        override var hasFinished: Boolean = false

        override fun start() {
            hotspot.loadContent()
            hasFinished = true
            batman.load()
            enterCinematic.reset()
            exitCinematic.reset()
        }

        override fun step() {

        }

    }

    override fun unloadContents() = object: StepAction {
        override var hasFinished: Boolean = false

        override fun start() {
            hotspot.unloadContent()
            batman.unload()
            hasFinished = true
        }

        override fun step() {
        }

    }

    override fun renderWorld(batch: SpriteBatch) {
        batman.render(batch)
        hotspot.render(LayerType.WORLD, batch)
    }

    override fun renderHud(batch: SpriteBatch) {
        hotspot.render(LayerType.HUD, batch)
    }


}