package io.czar.dbinfodemo.security

import io.czar.dbinfodemo.security.WebSecurityConfig.Companion.TOKEN_PREFIX
import io.jsonwebtoken.Jwts
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ConfigurationProperties("security.token")
class TokenBasedAuthorizationFilter internal constructor(authenticationManager: AuthenticationManager) : BasicAuthenticationFilter(authenticationManager) {
	lateinit var secret: String
	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

		var authorizationToken: String? = request.getHeader(HttpHeaders.AUTHORIZATION)

		if (authorizationToken != null && authorizationToken.startsWith(TOKEN_PREFIX)) {
			authorizationToken = authorizationToken.replaceFirst(TOKEN_PREFIX.toRegex(), "")

			val username = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(authorizationToken)
					.body.subject

			SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(username, null, emptyList<GrantedAuthority>())
		}

		chain.doFilter(request, response)
	}
}
