package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.model.PostgreSettings
import io.czar.dbinfodemo.model.PostgreSettingsRepository
import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.services.CurrentUser
import mu.KLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/user/configurations")
class UserConfigurationsController(
		val dbConfigurations: PostgreSettingsRepository) {

	companion object : KLogging()

	@PostMapping
	fun addConfiguration(@CurrentUser user: UserAccount, @RequestBody config: PostgreSettings) {
		logger.info("user: {name: ${user.username}, id: ${user.id}}")
		logger.info("config: $config")
		dbConfigurations.save(config.apply { userId = checkNotNull(user.id) })
	}
}