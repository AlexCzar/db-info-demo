package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.services.UserRegistrationService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


@RestController
class RegistrationController(
		private val registrationService: UserRegistrationService) {

	@PostMapping("/register")
	fun register(user: UserRegistration, request: HttpServletRequest) {
		registrationService.register(user)
		authenticateUserAndSetSession(user, request)
	}

	private fun authenticateUserAndSetSession(user: UserRegistration, request: HttpServletRequest) {
		val token = UsernamePasswordAuthenticationToken(user.username, user.password)
		// generate session if one doesn't exist
		request.session
		token.details = WebAuthenticationDetails(request)
//		SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(token)
	}
}

data class UserRegistration(
		val username: String,
		val password: String
)