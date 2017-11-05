package io.czar.dbinfodemo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class HelloController {

	@GetMapping("/hello")
	fun sayHi(principal: Principal) = "Hello, ${principal.name}!"
}