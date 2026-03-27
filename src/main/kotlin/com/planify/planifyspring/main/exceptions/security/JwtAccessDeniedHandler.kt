package com.planify.planifyspring.main.exceptions.security

import com.planify.planifyspring.main.common.utils.writeApplicationResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.writeApplicationResponse<Nothing>(
            httpStatus = HttpStatus.FORBIDDEN,
            appCode = 3014,
            message = "Access denied"
        )
    }
}
