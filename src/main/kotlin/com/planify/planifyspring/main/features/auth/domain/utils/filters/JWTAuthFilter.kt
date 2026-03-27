package com.planify.planifyspring.main.features.auth.domain.utils.filters

import com.planify.planifyspring.main.features.auth.domain.exceptions.AuthorizationTokenNotSpecifiedHttpException
import com.planify.planifyspring.main.features.auth.domain.exceptions.AuthorizationTypeUnknownHttpException
import com.planify.planifyspring.main.features.auth.domain.use_cases.AuthUseCaseGroup
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JWTAuthFilter(
    private val authUseCaseGroup: AuthUseCaseGroup
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader("Authorization")

        if (header == null) {
            filterChain.doFilter(request, response)
            return
        }

        if (!header.startsWith("Bearer ")) throw AuthorizationTypeUnknownHttpException("Unknown authorization type")

        val token = header.substring(7)
        if (token.isEmpty()) throw AuthorizationTokenNotSpecifiedHttpException("Authorization token is missing")

        val authContext = authUseCaseGroup.authenticate(token)

        val authorities = authContext.accessInfo.roles.map { SimpleGrantedAuthority(it.name) } + authContext.accessInfo.authorities.map { SimpleGrantedAuthority(it.name) }
        val auth = UsernamePasswordAuthenticationToken(authContext, null, authorities)

        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
    }
}
