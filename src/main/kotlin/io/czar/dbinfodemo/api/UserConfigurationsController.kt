package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.model.PostgreSettings
import io.czar.dbinfodemo.model.PostgreSettingsRepository
import io.czar.dbinfodemo.model.UserAccount
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user/configurations")
class UserConfigurationsController(
		val dbConfigurations: PostgreSettingsRepository) {
	private val log = LoggerFactory.getLogger(UserConfigurationsController::class.java)
	@PostMapping
	fun addConfiguration(user: UserAccount, @RequestBody config: PostgreSettings) {
		log.info("user: {name: ${user.username}, id: ${user.id}}")
		log.info("config: $config")
		dbConfigurations.save(config.apply { userId = checkNotNull(user.id) })
	}
}