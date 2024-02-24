package com.mygdx.game.engine.stdx

class Pool<T, K, V>(
    private val fetch: (key: K) -> T,
    private val construct: (key: K, value: T, free: (value: V) -> Unit) -> V,
    private val free: (value: V) -> Unit
) where V: Rc<T, K>{

    private val cache: MutableMap<K, V> = mutableMapOf()

    operator fun get(key: K): V {
        if (!cache.containsKey(key)) {
            cache[key] = construct(
                key,
                fetch(key)
            ) {
                free(it)
                cache.remove(it.key)
            }
        }
        val entry = cache[key]!!
        entry.acquire()
        return entry
    }
}