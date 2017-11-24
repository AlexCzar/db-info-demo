package io.czar.dbinfodemo

import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath
import io.czar.dbinfodemo.utils.Credentials
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.http.HttpStatus

class DatabaseAccessTest : BasicTest() {
	val credentials: Credentials by lazy {
		restClient.login("test", "test")
	}

	@Test
	fun `connecting to database`() {
		val response = restClient.get<String>("/user/database/dbinfo/check", credentials)
		response.assertStatus(HttpStatus.OK)
		assertThat(response.body, Matchers.`is`("Ok"))
	}

	@Test
	fun `listing tables`() {
		val response = restClient.get<String>("/user/database/dbinfo/listTables", credentials)
		response.assertStatus(HttpStatus.OK)
		val responseBody = response.body
		// number reasonably close to a number returned by a postgresql database with freshly created user and tablespace
		val emptyUserTablesUsualCount = 150
		assertThat(responseBody, isJson(withJsonPath("$.length()", greaterThanOrEqualTo(emptyUserTablesUsualCount))))
	}

	@Test
	fun `listing tables with schema filter`() {
		val response = restClient.get<String>("/user/database/dbinfo/listTables?schema=public", credentials)
		response.assertStatus(HttpStatus.OK)
		println(response.body)
		assertThat(response.body, isJson(withJsonPath("$.length()", equalTo(1))))
	}

}