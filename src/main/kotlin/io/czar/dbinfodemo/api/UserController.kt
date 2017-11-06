package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.model.UserAccount
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController {

	@GetMapping
	fun sayHi(user: UserAccount): String {
		return """Hello, ${user.username}!
			|
			|You have following configurations set up:
			|
			|${user.configurations}
		""".trimMargin()
	}
}