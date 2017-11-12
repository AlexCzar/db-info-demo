package io.czar.dbinfodemo

import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(
		classes = arrayOf(DbInfoDemo::class),
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class HelloTest {
	@Autowired
	lateinit var testRestTemplate: TestRestTemplate
	private val restClient by lazy { TestRestClient(testRestTemplate) }

	@Test
	fun accessingSecureUserWithProperAuth() {
		val credentials = restClient.login("test", "test")
		val response = restClient.get<String>("/user", credentials)
		assertEquals(HttpStatus.OK, response.statusCode)
		assertThat(response.body, Matchers.`is`(
				"""Hello, test!
				|You have following configurations set up:
				|[PostgreSettings(id=2, userId=1, name=dbinfodemo, host=localhost, database=dbinfodemo, user=dbinfodemo, password=dbinfodemo, url=null)]"""
						.trimMargin()))
	}

	@Test
	fun accessingSecureUserWithoutAuth() {
		val credentials = EmptyCredentials
		val response = restClient.get<String>("/user", credentials)
		assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
		assertThat(response.body, Matchers.containsString(""""error":"Unauthorized""""))
	}

	@Test
	fun publicAccessWorks() {
		val response = restClient.get<String>("/public", EmptyCredentials)
		assertEquals(HttpStatus.OK, response.statusCode)
		assertEquals(response.body, "Hi, I'm public!")
	}
}