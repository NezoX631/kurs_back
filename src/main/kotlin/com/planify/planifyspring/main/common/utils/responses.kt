package com.planify.planifyspring.main.common.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.planify.planifyspring.main.common.entities.ApplicationResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

private val objectMapper = jacksonObjectMapper()

fun <T> T.asSuccessApplicationResponse(
    message: String = "Success",
    statusCode: Int = 1000
): ApplicationResponse<T> = ApplicationResponse(
    ok = true,
    appCode = statusCode,
    message = message,
    data = this
)

fun asErrorResponse(
    message: String,
    statusCode: Int
): ApplicationResponse<Nothing> = ApplicationResponse(
    ok = false,
    appCode = statusCode,
    message = message,
    data = null
)

fun <T> HttpServletResponse.writeApplicationResponse(
    ok: Boolean? = null,
    httpStatus: HttpStatus,
    appCode: Int,
    message: String? = null,
    data: T? = null
) {
    this.status = httpStatus.value()
    this.contentType = "application/json"

    outputStream.use {
        it.write(
            objectMapper.writeValueAsBytes(
                ApplicationResponse(
                    ok = ok ?: httpStatus.is2xxSuccessful,
                    appCode = appCode,
                    message = message ?: "[${httpStatus.value()}] ${httpStatus.reasonPhrase}",
                    data = data
                )
            )
        )
    }
}
