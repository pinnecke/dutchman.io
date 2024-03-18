package com.mygdx.game.engine.memory

import com.mygdx.game.engine.utils.info

interface ManagedContent {

    val id: String

    fun loadContent()
    fun unloadContent()
    // TODO fun resetContent()
}

fun managedContentOf(
    contentIdentifier: String,
    load: () -> Unit,
    unload: () -> Unit
) = object : ManagedContent {

    override val id = contentIdentifier

    override fun loadContent() {
        load()
        // TODO resetContent()
    }
    override fun unloadContent() = unload()
}

abstract class AllocatorManagedContent(override val id: String) : ManagedContent {

    private val allocator = ContentAllocator("$id (allocator)")

    protected abstract val managedContent: MutableList<ManagedContent>

    override fun loadContent() {
        allocator.register(managedContent)
        allocator.allocate()
    }

    override fun unloadContent() {
        allocator.deallocate()
        allocator.unregisterAll()
    }

}

class ContentAllocator(private val allocatorIdentifier: String) {

    private val managedContent = mutableSetOf<ManagedContent>()

    fun register(content: List<ManagedContent>) {
        managedContent.addAll(content)
    }

    fun unregisterAll() {
        managedContent.clear()
    }

    fun allocate() {
        info(t("start allocating..."))
        managedContent.forEach {
            info(t("loading ${it.id}"))
            it.loadContent()
        }
    }

    fun deallocate() {
        info(t("start deallocating..."))
        managedContent.reversed().forEach {
            info(t("unloading ${it.id}"))
            it.unloadContent()
        }
    }

    private fun t(msg: String) = "$allocatorIdentifier - $msg"
}