package com.planify.planifyspring.main.common.filters

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.common.utils.writeApplicationResponse
import com.planify.planifyspring.main.exceptions.ApplicationHttpException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ApplicationHttpExceptionHandlerFilter : OncePerRequestFilter() {
    private fun handleApplicationHttpException(
        exception: ApplicationHttpException,
        response: HttpServletResponse,
    ) {
        response.writeApplicationResponse<Nothing>(
            ok=false,
            httpStatus = exception.httpStatus,
            appCode = exception.appCode,
            message = "[${exception.httpStatus.value()}] ${exception.httpStatus.reasonPhrase}: ${exception.message}",
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (exception: ApplicationHttpException) {
            handleApplicationHttpException(exception, response)
        }
    }
}
