package io.czar.dbinfodemo.utils

import io.czar.dbinfodemo.security.WebSecurityConfig
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import java.net.URI
import java.util.*

class TestRestClient(val rest: TestRestTemplate) {

	inline fun <reified T> get(restPath: String, credentials: Credentials): ResponseEntity<T> {
		val headers = HttpHeaders()
		headers.add(HttpHeaders.AUTHORIZATION, credentials.token)
		return rest.exchange(restPath, GET, HttpEntity<Any>(headers), T::class.java)
	}

	inline fun <reified T> post(
		restPath: String,
		credentials: Credentials,
		body: Any,
		csrfToken: String?
	): ResponseEntity<T> {
		val headers = HttpHeaders().apply {
			add(HttpHeaders.AUTHORIZATION, credentials.token)
			if (csrfToken != null) {
				set(HttpHeaders.COOKIE, WebSecurityConfig.CSRF_COOKIE + "=" + csrfToken)
				set(WebSecurityConfig.CSRF_HEADER, csrfToken)
			}
		}

		return rest.exchange(restPath, POST, HttpEntity(body, headers), T::class.java)
	}

	fun login(username: String, password: String): Credentials = rest.execute(URI("/login"), POST,
		{ request: ClientHttpRequest ->
			with(request.body) {
				write("username=$username&password=$password".toByteArray())
				flush()
				close()
			}
			val csrfToken = UUID.randomUUID().toString()
			with(request.headers) {
				set(HttpHeaders.COOKIE, "${WebSecurityConfig.CSRF_COOKIE}=$csrfToken")
				set(WebSecurityConfig.CSRF_HEADER, csrfToken)
			}
		}, { response: ClientHttpResponse -> Credentials(response.headers[HttpHeaders.AUTHORIZATION]?.get(0)) })

}

open class Credentials(val token: String?)
object EmptyCredentials : Credentials(null)
