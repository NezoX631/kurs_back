package com.planify.planifyspring.main.features.actions.domain.exceptions

import com.planify.planifyspring.main.exceptions.ApplicationHttpException
import org.springframework.http.HttpStatus

open class BadActionIdHttpException(
    message: String?,
    appCode: Int = 6100
) : ApplicationHttpException(
    httpStatus = HttpStatus.BAD_REQUEST,
    appCode = appCode,
    message = message
)
