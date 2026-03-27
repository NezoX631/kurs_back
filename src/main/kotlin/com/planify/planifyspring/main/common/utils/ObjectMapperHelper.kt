package com.planify.planifyspring.main.common.utils

import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import kotlin.reflect.full.memberProperties

@Component
class ObjectMapperHelper(
    private val objectMapper: ObjectMapper
) {
    fun <T : Any> convertToStringsMap(value: T): Map<String, String> {
        return value::class.memberProperties.associate { prop ->
            val fieldName = prop.name
            val fieldValue = prop.getter.call(value)

            val stringValue = when (fieldValue) {
                null -> "null"
                is String -> fieldValue
                is Number, is Boolean -> fieldValue.toString()
                else -> objectMapper.writeValueAsString(fieldValue)
            }

            fieldName to stringValue
        }
    }

    fun <T : Any> convertFromStringsMap(value: Map<String, String>, clazz: Class<T>): T {
        val parsedMap = value.mapValues { (_, v) ->
            try {
                objectMapper.readValue(v, Any::class.java)
            } catch (_: Exception) {
                v
            }
        }

        return objectMapper.convertValue(parsedMap, clazz)
    }

    fun <T : Any> convertToString(value: T): String {
        return objectMapper.writeValueAsString(value)
    }

    fun <T : Any> convertFromString(value: String, clazz: Class<T>): T {
        return objectMapper.readValue(value, clazz)
    }
}