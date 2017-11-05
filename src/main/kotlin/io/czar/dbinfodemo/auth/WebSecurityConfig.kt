package io.czar.dbinfodemo.auth

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
		val userAccountRepository: UserAccountRepository
) : WebMvcConfigurer {

	@Bean
	fun userDetailsService() = UserDetailsService { username: String ->
		userAccountRepository.findByUsernameLowerCased(username.toLowerCase())?.toUserDetails()
				?: throw UsernameNotFoundException("Could not find user with username $username")
	}

	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()

}


private fun UserAccount.toUserDetails(): UserDetails = User(
		username, password, enabled,
		true, true, true,
		setOf(SimpleGrantedAuthority("USER")))
