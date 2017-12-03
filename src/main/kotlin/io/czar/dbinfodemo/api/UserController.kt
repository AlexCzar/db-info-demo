package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.services.CurrentUser
import io.czar.dbinfodemo.services.SqlDatabaseAccessService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
		val databaseAccessService: SqlDatabaseAccessService
) {

	@GetMapping
	fun getUserData(@CurrentUser user: UserAccount): String = user.toString()

	@GetMapping("/database/{dbName}/check")
	fun checkDatabaseConnection(@CurrentUser user: UserAccount, @PathVariable("dbName") dbName: String) =
			databaseAccessService.checkConnection(dbName, user)

	@GetMapping("/database/{dbName}/listTables")
	fun listDatabaseTables(
			@CurrentUser user: UserAccount,
			@PathVariable("dbName") dbName: String,
			@RequestParam("schema") schema: String?,
			@RequestParam("type") types: List<String>?) =
			databaseAccessService.listTables(dbName, schema, user, types?.map { it.toUpperCase() })
}