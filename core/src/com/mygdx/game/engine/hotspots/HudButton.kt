package com.mygdx.game.engine.hotspots

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Polygon
import com.mygdx.game.engine.EventFilter
import com.mygdx.game.engine.Hotspot
import com.mygdx.game.engine.LayerType
import com.mygdx.game.engine.Screen

class HudButton(
    x: Float,
    y: Float,
    region: Polygon,
    owner: Screen,
    filter: EventFilter
): Hotspot(LayerType.WORLD, x, y, region, owner, filter) {

    override fun load() {

    }

    override fun unload() {

    }

    override fun update(dt: Float) {

    }

    override fun render(batch: SpriteBatch) {

    }
}