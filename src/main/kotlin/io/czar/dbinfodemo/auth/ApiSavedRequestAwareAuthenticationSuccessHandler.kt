package io.czar.dbinfodemo.auth

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.HttpSessionRequestCache
import org.springframework.security.web.savedrequest.RequestCache
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class ApiSavedRequestAwareAuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

	private var requestCache: RequestCache = HttpSessionRequestCache()

	override fun onAuthenticationSuccess(
			request: HttpServletRequest,
			response: HttpServletResponse,
			authentication: Authentication) {

		val savedRequest = requestCache.getRequest(request, response)

		if (savedRequest == null) {
			clearAuthenticationAttributes(request)
			return
		}
		if (isAlwaysUseDefaultTargetUrl || targetUrlParameter != null && request.getParameter(targetUrlParameter)?.isNotEmpty() == true) {
			requestCache.removeRequest(request, response)
			clearAuthenticationAttributes(request)
			return
		}

		clearAuthenticationAttributes(request)
	}

}