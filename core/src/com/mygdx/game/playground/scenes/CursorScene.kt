package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.memory.managedContentOf
import com.mygdx.game.engine.objects.Decal
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.objects.Position
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene


class CursorScene(
    sceneManager: SceneManager,
): Scene(
    "Cursor Scene",
    sceneManager,
    clearColor = Color.BLACK,
    defaultPanelDimension = 2000f,
    defaultPanelType = PanelDimension.WIDTH
) {
    private val sequence = SequenceController(this)
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Cursor\n" +
        "[1] show\n" +
        "[2] hide\n" +
        "[3] hotspot on\n" +
        "[4] hotspot off\n" +
        "\n\n" +
        "[ESC] go main menu",
        Color.WHITE,
        200f, 390f,
        borderColor = Engine.colors.darkDimmedGray,
        borderWidth = 3f
    )

    init {
        manageContent(
            instructions,
            managedContentOf(
                id = "blend out cursor",
                load = { controller.cursor.visible = false },
                unload = { }
            )
        )

        input[Input.Keys.ESCAPE] = { sequence.switch(MainMenuScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            controller.cursor.visible = true
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            controller.cursor.visible = false
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            controller.cursor.hotspot = true
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            controller.cursor.hotspot = false
        }
    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }

}