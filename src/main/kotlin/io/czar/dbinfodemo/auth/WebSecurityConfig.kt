package io.czar.dbinfodemo.auth

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
		val userAccountRepository: UserAccountRepository
) : WebSecurityConfigurerAdapter() {


	override fun userDetailsService() = UserDetailsService { username: String ->
		userAccountRepository.findByUsernameLowerCased(username.toLowerCase())?.toUserDetails()
				?: throw UsernameNotFoundException("Could not find user with username $username")
	}

	@Bean
	fun restAuthenticationEntryPoint() = AuthenticationEntryPoint { _, response, _ ->
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
	}

	override fun configure(http: HttpSecurity) {
		http
				.csrf().disable()
				.exceptionHandling()
				.authenticationEntryPoint(restAuthenticationEntryPoint())
				.and()
				.authorizeRequests()
				.antMatchers("/api/user", "/api/user/**").authenticated()
				.and()
				.formLogin()
				.successHandler(authenticationSuccessHandler())
				.failureHandler(SimpleUrlAuthenticationFailureHandler())
				.and()
				.logout()
	}

	@Bean
	fun authenticationSuccessHandler() = ApiSavedRequestAwareAuthenticationSuccessHandler()
}

private fun UserAccount.toUserDetails(): UserDetails = User(
		username, password, enabled,
		true, true, true,
		setOf(SimpleGrantedAuthority("USER")))

