package io.czar.dbinfodemo

import io.czar.dbinfodemo.utils.EmptyCredentials
import io.czar.dbinfodemo.utils.MatchesPattern.Companion.matchesPattern
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.http.HttpStatus

class SecurityTest : BasicTest() {

	@Test
	fun `accessing secure user with proper auth`() {
		val credentials = restClient.login("test", "test")
		val response = restClient.get<String>("/user", credentials)
		response.assertStatus(HttpStatus.OK)
		val testUserRegex = """
			UserAccount\(id=1, username=test, password=.+, enabled=true, configurations=\[
			PostgreSettings\(id=2, userId=1, name=dbinfodemo, host=localhost, database=dbinfodemo,
			 user=dbinfodemo, password=dbinfodemo, url=null\)\]\)
					""".trimIndent().replace("\n","")
		assertThat(response.body, matchesPattern(testUserRegex))
	}

	@Test
	fun `accessing secure URI without auth`() {
		val credentials = EmptyCredentials
		val response = restClient.get<String>("/user", credentials)
		response.assertStatus(HttpStatus.UNAUTHORIZED)
		assertThat(response.body, containsString(""""error":"Unauthorized""""))
	}

	@Test
	fun `public access`() {
		val response = restClient.get<String>("/public", EmptyCredentials)
		response.assertStatus(HttpStatus.OK)
		assertEquals(response.body, "Hi, I'm public!")
	}
}