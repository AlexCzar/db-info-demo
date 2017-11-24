package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.services.JdbcTemplateDatabaseAccessService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
		val databaseAccessService: JdbcTemplateDatabaseAccessService
) {

	@GetMapping
	fun getUserData(user: UserAccount): String = user.toString()

	@GetMapping("/database/{dbName}/check")
	fun checkDatabaseConnection(user: UserAccount, @PathVariable("dbName") dbName: String) =
			databaseAccessService.checkConnection(dbName)

	@GetMapping("/database/{dbName}/listTables")
	fun listDatabaseTables(
			user: UserAccount,
			@PathVariable("dbName") dbName: String,
			@RequestParam("schema") schema: String?) =
			databaseAccessService.listTables(dbName, schema)
}