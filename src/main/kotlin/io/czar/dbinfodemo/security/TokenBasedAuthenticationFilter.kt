package io.czar.dbinfodemo.security

import io.czar.dbinfodemo.security.WebSecurityConfig.Companion.TOKEN_LIFETIME
import io.czar.dbinfodemo.security.WebSecurityConfig.Companion.TOKEN_PREFIX
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ConfigurationProperties(prefix = "security.token")
class TokenBasedAuthenticationFilter(authenticationManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {
	/**
	 * Secret to use for JWT encryption.
	 */
	lateinit var secret: String

	init {
		setAuthenticationManager(authenticationManager)
	}

	override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?,
										  authentication: Authentication) {
		val identifiedUser = authentication.principal as IdentifiedUser
		val token = Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setSubject(identifiedUser.username)
				.claim("userId", identifiedUser.id)
				.setExpiration(Date(System.currentTimeMillis() + TOKEN_LIFETIME))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact()

		response.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
	}
}
