package com.planify.planifyspring.main.common.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TrailingSlashHandlerFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestUri: String = request.requestURI

        if (requestUri.endsWith("/")) {
            val queryString = request.queryString?.let { "?$it" } ?: ""
            val newUrl = requestUri.substring(0, requestUri.length - 1) + queryString

            response.status = HttpStatus.PERMANENT_REDIRECT.value()
            response.setHeader(HttpHeaders.LOCATION, newUrl)
            return
        }

        filterChain.doFilter(request, response)
    }
}
