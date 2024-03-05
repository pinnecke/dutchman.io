package com.mygdx.game.playground.scenes

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.game.engine.*
import com.mygdx.game.engine.objects.FrameAnimation
import com.mygdx.game.engine.objects.Label
import com.mygdx.game.engine.utils.GdxKeyboardInputUtil
import com.mygdx.game.playground.MainMenuScene

class CameraModeScene: Scene(
    clearColor = Color.GRAY
) {
    private val controller = SceneController(this)
    private val input = GdxKeyboardInputUtil()

    private val instructions = Label(
        "Press 1 to enter cinematic mode, and 2 to leave cinematic mode\nESC key enters main scene",
        Color.BLACK,
        Engine.canvas.safeZone.left + 50f, Engine.canvas.safeZone.height - 50f,
    )

    private var page = FrameAnimation(
        name = "turtles",
        sheets = super.sheets
    )

    private val developerSurfaceScale = 1.9f
    private val developerShot = CameraShot(
        shot = Surface2D(
            left = Engine.canvas.safeZone.left - 1500f,
            bottom = Engine.canvas.safeZone.bottom - 200f,
            width = developerSurfaceScale * Engine.canvas.safeZone.width,
            height = developerSurfaceScale * Engine.canvas.safeZone.height
        )
    )

    private val splashPanelShot = CameraShot(
        shot = Surface2D(
            left = 50f,
            bottom = 50f,
            width = 1300f,
            height = 920f
        )
    )

    private val closeUpShot1 = CameraShot(
        shot = Surface2D(
            left = 150f,
            bottom = 1500f,
            width = 500f,
            height = 450f
        )
    )

    private val closeUpShot2 = CameraShot(
        shot = Surface2D(
            left = 750f,
            bottom = 1500f,
            width = 480f,
            height = 450f
        )
    )

    private val closeUpShot3 = CameraShot(
        shot = Surface2D(
            left = 150f,
            bottom = 1025f,
            width = 550f,
            height = 350f
        )
    )

    private val closeUpShot4 = CameraShot(
        shot = Surface2D(
            left = 830f,
            bottom = 1025f,
            width = 400f,
            height = 350f
        )
    )

    override fun create() {
        instructions.create()
        page.create()
        developerShot.create()
        splashPanelShot.create()
        closeUpShot1.create()
        closeUpShot2.create()
        closeUpShot3.create()
        closeUpShot4.create()

        input[Input.Keys.NUM_1] = { controller.setCameraShot(closeUpShot1) }
        input[Input.Keys.NUM_2] = { controller.setCameraShot(closeUpShot2) }
        input[Input.Keys.NUM_3] = { controller.setCameraShot(closeUpShot3) }
        input[Input.Keys.NUM_4] = { controller.setCameraShot(closeUpShot4) }
        input[Input.Keys.NUM_5] = { controller.setCameraShot(splashPanelShot) }
        input[Input.Keys.NUM_6] = { controller.setCameraShot(developerShot) }
        input[Input.Keys.ESCAPE] = { controller.enterScene(MainMenuScene::class) }
    }

    override fun render(batch: SpriteBatch) {
        input.act()
        page.render(batch)

        developerShot.render(batch)
        splashPanelShot.render(batch)
        closeUpShot1.render(batch)
        closeUpShot2.render(batch)
        closeUpShot3.render(batch)
        closeUpShot4.render(batch)
    }

    override fun overlay(batch: SpriteBatch) {
        instructions.render(batch)
    }

    override fun destroy(){
        instructions.destroy()
        page.destroy()
        developerShot.destroy()
        splashPanelShot.destroy()
        closeUpShot1.destroy()
        closeUpShot2.destroy()
        closeUpShot3.destroy()
        closeUpShot4.destroy()
    }

}