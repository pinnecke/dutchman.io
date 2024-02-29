package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.Label

class DimScreen: Screen(
    backPanelColor = Color(0f, 165f/255f, 227f/255f, 1.0f)
) {

    private val instructions = Label(
        "Press 0, 1,..., 9 to dim screen to 0%, 10%, ..., 100%\n\nM key is dimming speed medium\nS key is dimming speed slow\nH key is dimming speed high\nESC key enters main scene",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height / 2f,
    )

    private val overlay = Label(
        "This is an overlay",
        Color.WHITE,
        300f, Engine.canvas.safeZone.height - 200f,
    )

    private var speed = ScreenDimmer.DimSpeed.MEDIUM

    override fun update(dt: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            speed = ScreenDimmer.DimSpeed.SLOW
        } else if (Gdx.input.isKeyPressed(Input.Keys.M)) {
            speed = ScreenDimmer.DimSpeed.MEDIUM
        } else if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            speed = ScreenDimmer.DimSpeed.HIGH
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
            dimScene(0.0f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            dimScene(0.1f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            dimScene(0.2f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            dimScene(0.3f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            dimScene(0.4f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            dimScene(0.5f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            dimScene(0.6f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_7)) {
            dimScene(0.7f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_8)) {
            dimScene(0.8f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_9)) {
            dimScene(1.0f, speed)
        } else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterScene(GameMenuScreen::class)
        }
    }

    override fun render(batch: SpriteBatch) {
        instructions.render(batch)
    }

    override fun renderOverlay(batch: SpriteBatch) {
        overlay.render(batch)
    }

    override fun loadContents() {
        instructions.create()
        overlay.create()
    }

    override fun unloadContents(){
        instructions.destroy()
        overlay.destroy()
    }



}