package io.czar.dbinfodemo

import com.jayway.jsonpath.matchers.JsonPathMatchers.isJson
import com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath
import io.czar.dbinfodemo.utils.Credentials
import mu.KLogging
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.http.HttpStatus

/**
 * Please see README.adoc Setup section!
 * Tests for [io.czar.dbinfodemo.services.MetaDataSqlDatabaseAccessService]
 */
class DatabaseAccessTest : BasicTest() {
	val credentials: Credentials by lazy {
		restClient.login("test", "test")
	}

	companion object : KLogging()

	@Test
	fun `connecting to database`() {
		restClient.get<String>("/user/database/dbinfodemo/check", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(body, Matchers.`is`("Connected"))
		}
	}

	@Test
	fun `listing tables`() {
		val `reasonable minimum number of tables in default postgres setup` = 300
		restClient.get<String>("/user/database/dbinfodemo/listTables", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(
				body, isJson(
					withJsonPath(
						"$.length()", greaterThanOrEqualTo(
							`reasonable minimum number of tables in default postgres setup`
						)
					)
				)
			)
		}
	}

	@Test
	fun `listing tables with schema filter`() {
		restClient.get<String>("/user/database/dbinfodemo/listTables?schema=public", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(body, isJson(withJsonPath("$.length()", equalTo(4))))
		}
	}

	@Test
	fun `listing tables with schema and type filters`() {
		restClient.get<String>("/user/database/dbinfodemo/listTables?schema=public&type=table", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(body, isJson(withJsonPath("$.length()", equalTo(2))))
		}
	}

	@Test
	fun `previewing table`() {
		restClient.get<String>("/user/database/dbinfodemo/public/orders", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(
				body, isJson(
					allOf(
						withJsonPath("$.columns.length()", equalTo(3)),
						withJsonPath("$.columns.*", equalTo(listOf("order_id", "product_id", "quantity"))),
						withJsonPath("$.rows.length()", equalTo(3)),
						withJsonPath(
							"$.rows.*", equalTo(
								listOf(
									listOf(1, 1, 1),
									listOf(2, 1, 3),
									listOf(3, 1, 2)
								)
							)
						)
					)
				)
			)
		}
	}

	@Test
	fun `previewing table with limit`() {
		restClient.get<String>("/user/database/dbinfodemo/public/orders?limit=2", credentials).run {
			assertStatus(HttpStatus.OK)
			assertThat(
				body, isJson(
					allOf(
						withJsonPath("$.rows.length()", equalTo(2)),
						withJsonPath(
							"$.rows.*", equalTo(
								listOf(
									listOf(1, 1, 1),
									listOf(2, 1, 3)
								)
							)
						)
					)
				)
			)
		}
	}
}