package com.planify.planifyspring.main.common.utils

import org.springframework.cache.Cache
import tools.jackson.databind.ObjectMapper
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

class JsonCacheWrapper(
    val delegate: Cache,
    val objectMapper: ObjectMapper
) : Cache by delegate {

    inline fun <reified T> getAs(key: String): T? {
        val wrapper: Cache.ValueWrapper? = delegate.get(key)
        return wrapper?.let {
            objectMapper.convertValue(it.get(), T::class.java)
        }
    }

    fun put(key: String, value: Any?) = delegate.put(key, value)
    override fun <T : Any> retrieve(key: Any, valueLoader: Supplier<CompletableFuture<T>>): CompletableFuture<T> = delegate.retrieve(key, valueLoader)
    override fun retrieve(key: Any): CompletableFuture<*>? = delegate.retrieve(key)
    override fun putIfAbsent(key: Any, value: Any?): Cache.ValueWrapper? = delegate.putIfAbsent(key, value)
    override fun evictIfPresent(key: Any): Boolean = delegate.evictIfPresent(key)
    override fun invalidate(): Boolean = delegate.invalidate()
}
