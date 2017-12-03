package io.czar.dbinfodemo.security

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
class WebMvcConfig : WebMvcConfigurer {
	override fun addViewControllers(registry: ViewControllerRegistry) {
		registry.addRedirectViewController("/", "/user")
	}

}

@Configuration
@EnableWebSecurity
@ConfigurationProperties("security")
class WebSecurityConfig(
		private val userAccountRepository: UserAccountRepository,
		@Value("\${security.csrfEnabled:true}")
		private val csrfEnabled: Boolean
) : WebSecurityConfigurerAdapter() {

	companion object {
		const val CSRF_COOKIE = "CSRF-TOKEN"
		const val CSRF_HEADER = "X-CSRF-TOKEN"
		// 14 days
		const val TOKEN_LIFETIME: Long = 14 * 24 * 3600 * 1000
		const val TOKEN_PREFIX = "Bearer "
	}

	@Bean
	fun accessDeniedHandler() = AccessDeniedHandlerImpl()

	@Bean
	fun statelessCsrfFilter() = StatelessCsrfFilter()

	@Autowired
	override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
		authenticationManagerBuilder
				.userDetailsService(userDetailsService())
				.passwordEncoder(passwordEncoder())

	}

	override fun configure(http: HttpSecurity): Unit = with(http) {
		val configurer = csrf().disable()
		if (csrfEnabled) {
			configurer.addFilterBefore(statelessCsrfFilter(), CsrfFilter::class.java)
		}
		exceptionHandling().authenticationEntryPoint { _: HttpServletRequest, response: HttpServletResponse, _: AuthenticationException ->
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
		}

		httpBasic().disable()
				.formLogin().disable()
				.logout().disable()
				.addFilter(tokenBasedAuthenticationFilter())
				.addFilter(tokenBasedAuthorizationFilter())

		authorizeRequests()
				.antMatchers("/public/**").permitAll()
				.antMatchers("/**").authenticated()
	}

	@Bean
	fun tokenBasedAuthorizationFilter() = TokenBasedAuthorizationFilter(authenticationManager())

	@Bean
	fun tokenBasedAuthenticationFilter() = TokenBasedAuthenticationFilter(authenticationManager())

	@Bean
	override fun userDetailsService() = UserDetailsService { username: String ->
		userAccountRepository.findByUsernameLowerCased(username.toLowerCase())?.toUserDetails()
				?: throw UsernameNotFoundException("Could not find user with username $username")
	}

	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()

	@Bean
	fun restAuthenticationEntryPoint() =
			AuthenticationEntryPoint { _: HttpServletRequest, response: HttpServletResponse, _: AuthenticationException ->
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
			}

	@Bean
	fun authenticationSuccessHandler() = object : SimpleUrlAuthenticationSuccessHandler() {
		override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) =
				clearAuthenticationAttributes(request)
	}

	private fun UserAccount.toUserDetails(): UserDetails =
			IdentifiedUser(checkNotNull(id), username, password, enabled)
}

class IdentifiedUser(
		val id: Long, username: String, password: String, enabled: Boolean
) : User(username, password, enabled, true, true, true, setOf(SimpleGrantedAuthority("USER"))), UserDetails

