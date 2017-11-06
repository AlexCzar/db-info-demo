package io.czar.dbinfodemo

import io.czar.dbinfodemo.model.UserAccount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

	@GetMapping("/")
	fun sayHi(user: UserAccount): String {
		return """Hello, ${user.username}!
			|You have following configurations set up:
			|${user.configurations}
		""".trimMargin()
	}
}