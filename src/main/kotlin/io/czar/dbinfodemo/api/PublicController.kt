package io.czar.dbinfodemo.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/public")
class PublicController {

	@GetMapping
	fun publicHello() = "Hi, I'm public!"
}