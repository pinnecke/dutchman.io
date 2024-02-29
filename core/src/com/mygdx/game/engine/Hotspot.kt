package com.mygdx.game.engine

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Polygon

fun closedPolygon(
    vararg vs: Float
): Polygon = Polygon(
    (vs.toList() + listOf(vs[0], vs[1])).toFloatArray()
)

data class TouchDownPredicate(
    val pointer: Int,
    val button: Int
) {
    companion object {
        fun any() = TouchDownPredicate(-1 , -1)
    }

    val isAny = pointer == -1 && button == -1
}

data class TouchDownFilter (
    val action: (button: Int, pointer: Int) -> Unit,
    val predicates: List<TouchDownPredicate>
)

data class EventFilter(
    val touchDownFilter: TouchDownFilter?
) {
    companion object {
        fun builder() = EventFilterBuilder()
    }
}

class TouchDownFilterBuilder(
    private val parent: EventFilterBuilder,
    private val action: (button: Int, pointer: Int) -> Unit
) {
    private val touchDowns = mutableListOf<TouchDownPredicate>()

    fun any(): TouchDownFilterBuilder {
        touchDowns.add(TouchDownPredicate.any())
        return this
    }

    fun any(pointer: Int, button: Int): TouchDownFilterBuilder {
        touchDowns.add(TouchDownPredicate(pointer, button))
        return this
    }

    fun build(): EventFilterBuilder {
        parent.touchDownFilter = TouchDownFilter(action, touchDowns)
        return parent
    }
}

class EventFilterBuilder {

    internal var touchDownFilter: TouchDownFilter? = null

    fun touchDown(action: (button: Int, pointer: Int) -> Unit) =
        TouchDownFilterBuilder(this, action)

    fun build() = EventFilter(
        touchDownFilter
    )

}

abstract class Hotspot(
    private val layer: LayerType,
    x: Float,
    y: Float,
    private val region: Polygon,
    private val owner: Screen,
    private val filter: EventFilter
): InputAdapter() {
    private var regionDebugRenderer = DebugRenderer(Config.DEBUG_RENDER_HOTSPOT_REGIONS, Color.RED)

    init {
        translate(x, y)
    }

    private var touched = false

    abstract fun load()
    abstract fun unload()
    abstract fun update(dt: Float)
    abstract fun render(batch: SpriteBatch)

    fun translate(x: Float, y: Float) {
        region.translate(x, y)
        region.transformedVertices
    }

    var x: Float = x
        get() = region.x

    var y: Float = y
        get() = region.y

    final override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (region.contains(screenX.toFloat(), screenY.toFloat()) &&
            filter.touchDownFilter != null
        ) {
            for (predicate in filter.touchDownFilter.predicates) {
                if (predicate.isAny || (predicate.button == button && predicate.pointer == pointer)) {
                    filter.touchDownFilter.action(button, pointer)
                    regionDebugRenderer.color = Color.WHITE
                    return true
                }
            }
        }
        return false
    }



    fun loadContent() {
        load()
        owner.registerInput(layer, this)
    }

    fun unloadContent() = unload()



    fun render(layer: LayerType, batch: SpriteBatch) {
        if (layer == this.layer) {
            render(batch)

            batch.end()

            if (regionDebugRenderer.enabled) {
                val vertices = region.vertices
                for (i in 0 .. region.vertexCount step 2) {
                    regionDebugRenderer.drawLine(
                        batch.projectionMatrix,
                        x + vertices[i], y + vertices[i + 1],
                        x + vertices[i + 2], y + vertices[i + 3],
                    )
                }
                regionDebugRenderer.drawLine(
                    batch.projectionMatrix,
                    x + vertices[vertices.size - 4], y + vertices[vertices.size - 3],
                    x + vertices[vertices.size - 2], y + vertices[vertices.size - 1],
                )
            }

            batch.begin()

        }
    }

}