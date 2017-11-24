package io.czar.dbinfodemo.services

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
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
	override fun supportsParameter(parameter: MethodParameter) =
			parameter.parameterType == UserAccount::class.java

	@Transactional(readOnly = true)
	override fun resolveArgument(
			parameter: MethodParameter, mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory
	): UserAccount = (webRequest.userPrincipal as? Authentication)?.principal.let {
		userAccountRepository.getOne(checkNotNull(it as? Long) { "User should have been identified at this point." })
	}
}