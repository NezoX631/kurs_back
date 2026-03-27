/*package com.planify.planifyspring.main.common.utils.redis

import com.planify.planifyspring.main.common.utils.ObjectMapperHelper
import org.springframework.data.redis.connection.stream.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Suppress("unused")
@Component
class RedisHelper(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapperHelper: ObjectMapperHelper
) {
    fun expire(key: String, ttl: Duration) {
        stringRedisTemplate.expire(key, ttl)
    }

    fun <T : Any> hsetField(key: String, field: String, value: T) {
        val jsonSting = objectMapperHelper.convertToString(value)
        stringRedisTemplate.opsForHash<String, String>().put(key, field, jsonSting)
    }

    fun hdel(key: String) {
        stringRedisTemplate.opsForHash<String, String>().delete(key)
    }

    fun <T : Any> hset(key: String, value: T) {
        val data = objectMapperHelper.convertToStringsMap(value)
        stringRedisTemplate.opsForHash<String, String>().putAll(key, data)
    }

    fun <T : Any> hget(key: String, clazz: Class<T>): T? {
        val raw = stringRedisTemplate.opsForHash<String, String>().entries(key)
        if (raw.isEmpty()) return null
        return objectMapperHelper.convertFromStringsMap(raw, clazz)
    }

    fun <T : Any> hgetAllSubkeys(base: String, clazz: Class<T>): List<T> {
        val keys = stringRedisTemplate.keys(base)

        val result = ArrayList<T>()
        for (key in keys) {
            val obj = hget(key, clazz)
            result.add(obj!!)
        }

        return result
    }

    fun <T : Any> set(key: String, value: T) {
        stringRedisTemplate.opsForValue().set(key, objectMapperHelper.convertToString(value))
    }

    fun <T : Any> get(key: String, clazz: Class<T>): T? {
        val value = stringRedisTemplate.opsForValue().get(key) ?: return null
        return objectMapperHelper.convertFromString(value, clazz)
    }

    fun createStreamGroup(
        key: String,
        group: String,
        readOffset: ReadOffset,
        ignoreBusyGroup: Boolean = true
    ) {
        try {
            stringRedisTemplate.opsForStream<String, String>().createGroup(key, readOffset, group)
        } catch (e: Exception) {
            val message = e.cause?.message ?: e.message
            if (message?.contains("BUSYGROUP") == true && ignoreBusyGroup) return  // Group already exists
            throw e
        }
    }

    fun <T : Any> addToStream(key: String, value: T): RecordId {
        return stringRedisTemplate.opsForStream<String, String>().add(key, objectMapperHelper.convertToStringsMap(value))
    }

    fun deleteFromStream(key: String, recordId: RecordId) {
        stringRedisTemplate.opsForStream<String, String>().delete(key, recordId)
    }

    fun <T : Any> readStream(
        key: String,
        offset: ReadOffset,
        count: Long,
        timeout: Long,
        clazz: Class<T>
    ): List<Pair<RecordId, T>> {
        val redis = stringRedisTemplate.opsForStream<String, String>()

        val readOptions = StreamReadOptions
            .empty()
            .count(count)
            .block(Duration.ofSeconds(timeout))

        val offset = StreamOffset.create(key, offset)
        val records = redis.read(readOptions, offset) ?: return emptyList()

        return records.mapNotNull { record ->
            record.id to objectMapperHelper.convertFromStringsMap(record.value, clazz)
        }
    }

    fun <T : Any> readAsConsumer(
        key: String,
        group: String,
        consumer: String,
        offset: ReadOffset,
        count: Long,
        timeout: Long,
        clazz: Class<T>
    ): List<T> {
        val redis = stringRedisTemplate.opsForStream<String, String>()

        val records = redis.read(
            Consumer.from(group, consumer),
            StreamReadOptions
                .empty()
                .count(count)
                .block(Duration.ofSeconds(timeout)),
            StreamOffset.create(key, offset),
        ) ?: return emptyList<T>()

        return records.mapNotNull { record ->
            objectMapperHelper.convertFromStringsMap(record.value, clazz)
        }
    }

    fun acknowledge(
        key: String,
        group: String,
        recordId: RecordId,
    ) {
        stringRedisTemplate.opsForStream<String, String>().acknowledge(key, group, recordId)
    }

    fun <T : Any> addToSet(key: String, value: T) {
        stringRedisTemplate.opsForSet().add(key, objectMapperHelper.convertToString(value))
    }

    fun <T : Any> getSet(key: String, clazz: Class<T>): List<T> {
        val values = stringRedisTemplate.opsForSet().members(key) ?: return emptyList()
        return values.mapNotNull { value -> objectMapperHelper.convertFromString(value, clazz) }
    }
}
*/