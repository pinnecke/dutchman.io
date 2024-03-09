package com.mygdx.game.engine.stdx

import com.mygdx.game.engine.memory.AllocatorManagedContent

abstract class GameObject(contentIdentifier: String): AllocatorManagedContent(contentIdentifier), Update, Render
