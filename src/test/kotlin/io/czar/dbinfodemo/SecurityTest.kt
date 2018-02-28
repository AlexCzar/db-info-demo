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
		val testUserRegex =
			"""UserAccount\(id=1, username=test, password=.+, enabled=true, configurations=\[PostgreSettings\(.+\)\]\)"""
		restClient.get<String>("/user", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(body, matchesPattern(testUserRegex))
		}
	}

	@Test
	fun `accessing secure URI without auth`() {
		val credentials = EmptyCredentials
		restClient.get<String>("/user", credentials).run {
			assertStatus(HttpStatus.UNAUTHORIZED)
			assertThat(body, containsString(""""error":"Unauthorized""""))
		}
	}

	@Test
	fun `public access`() {
		restClient.get<String>("/public", EmptyCredentials).run {
			assertStatus(HttpStatus.OK)
			assertEquals(body, "Hi, I'm public!")
		}
	}
}