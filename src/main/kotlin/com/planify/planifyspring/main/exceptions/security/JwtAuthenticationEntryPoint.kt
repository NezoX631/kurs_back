package com.planify.planifyspring.main.exceptions.security

import com.planify.planifyspring.main.common.utils.writeApplicationResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.writeApplicationResponse<Nothing>(
            httpStatus = HttpStatus.UNAUTHORIZED,
            appCode = 3001,
            message = "Authorization was not specified"
        )
    }
}
