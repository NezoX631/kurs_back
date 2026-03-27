package com.planify.planifyspring.main.exceptions

import org.springframework.http.HttpStatus

open class ApplicationHttpException(
    val httpStatus: HttpStatus,
    val appCode: Int,
    message: String?
) : RuntimeException(message)
