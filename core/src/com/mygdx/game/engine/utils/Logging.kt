package com.mygdx.game.engine.utils

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx

fun Any.info(msg: String) {
    Gdx.app.logLevel = Application.LOG_INFO
    Gdx.app.log(this::class.simpleName!!, msg)
}
