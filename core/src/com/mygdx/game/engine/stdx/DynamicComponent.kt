package com.mygdx.game.engine.stdx

interface DynamicComponent: Create, Destroy {
    val componentName: String
}