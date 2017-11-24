package io.czar.dbinfodemo

import io.czar.dbinfodemo.utils.TestRestClient
import org.junit.Assert
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(
		classes = arrayOf(DbInfoDemo::class),
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
abstract class BasicTest {
	@Autowired
	lateinit var testRestTemplate: TestRestTemplate
	protected val restClient by lazy { TestRestClient(testRestTemplate) }

	protected fun ResponseEntity<*>.assertStatus(status: HttpStatus, lazyMessage: (() -> String)? = null) {
		Assert.assertEquals(lazyMessage?.invoke(), status, statusCode)
	}
}