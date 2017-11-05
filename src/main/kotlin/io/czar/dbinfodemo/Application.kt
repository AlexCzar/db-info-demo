package io.czar.dbinfodemo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


fun main(args: Array<String>) {
	SpringApplication.run(Application::class.java, *args)
}

@SpringBootApplication
class Application

