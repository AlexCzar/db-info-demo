package io.czar.dbinfodemo.security

import io.czar.dbinfodemo.security.WebSecurityConfig.Companion.CSRF_COOKIE
import io.czar.dbinfodemo.security.WebSecurityConfig.Companion.CSRF_HEADER
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StatelessCsrfFilter : OncePerRequestFilter() {

	private val accessDeniedHandler = AccessDeniedHandlerImpl()

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

		if (csrfTokenIsRequired(request)) {
			val csrfHeaderToken: String? = request.getHeader(CSRF_HEADER)
			val csrfCookieToken: String? = request.cookies?.asSequence()?.find { it.name == CSRF_COOKIE }?.value

			if (csrfHeaderToken == null || csrfCookieToken == null || csrfCookieToken != csrfHeaderToken) {
				accessDeniedHandler.handle(request, response, AccessDeniedException("CSRF tokens missing or not matching"))
				return
			}

		}

		filterChain.doFilter(request, response)
	}

	private fun csrfTokenIsRequired(request: HttpServletRequest) = request.method !in csrfSafeMethods

	private val csrfSafeMethods = setOf("GET", "HEAD", "TRACE", "OPTIONS")
}
