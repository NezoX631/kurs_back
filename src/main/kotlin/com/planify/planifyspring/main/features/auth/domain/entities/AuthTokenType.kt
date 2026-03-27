package com.planify.planifyspring.main.features.auth.domain.entities

enum class AuthTokenType(val code: Int) {
    ACCESS(1),
    REFRESH(2);

    companion object {
        fun fromCode(code: Int): AuthTokenType? {
            return entries.firstOrNull { it.code == code }
        }
    }
}
