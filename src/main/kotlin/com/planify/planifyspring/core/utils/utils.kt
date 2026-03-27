package com.planify.planifyspring.core.utils

fun getRandomString(length: Int): String {
    val charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}
