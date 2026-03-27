package com.planify.planifyspring.main.common.entities

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApplicationResponse<T>(
    val ok: Boolean,
    val appCode: Int,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        fun success(): ApplicationResponse<Nothing> =
            ApplicationResponse(ok = true, appCode = 1000, message = "Success")

        fun <T> success(data: T): ApplicationResponse<T> =
            ApplicationResponse(ok = true, appCode = 1000, message = "Success", data = data)

        fun <T> error(appCode: Int, message: String): ApplicationResponse<T> =
            ApplicationResponse(ok = false, appCode = appCode, message = message)
    }
}