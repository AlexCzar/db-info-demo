package io.czar.dbinfodemo.services

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import mu.KLogging
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Service
class UserAccountArgumentResolver(
	val userAccountRepository: UserAccountRepository
) : HandlerMethodArgumentResolver {
	companion object : KLogging()

	override fun supportsParameter(parameter: MethodParameter) = with(parameter) {
		logger.info { parameter.parameterAnnotations.map { it.javaClass.name } }
		parameterType.isAssignableFrom(UserAccount::class.java) && hasParameterAnnotation(CurrentUser::class.java)
	}

	@Transactional(readOnly = true)
	override fun resolveArgument(
		parameter: MethodParameter,
		mavContainer: ModelAndViewContainer,
		webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory
	): UserAccount = (webRequest.userPrincipal as? Authentication)?.principal.let {
		userAccountRepository.getOne(checkNotNull(it as? Long) { "User should have been identified at this point." })
	}
}

/**
 * Marks UserAccount parameter to be auto-resolved in controller methods
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentUser