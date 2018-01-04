package io.czar.dbinfodemo.api

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.services.CurrentUser
import io.czar.dbinfodemo.services.SqlDatabaseAccessService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
open class UserController(
		private val databaseAccessService: SqlDatabaseAccessService
) {

	@GetMapping
	open fun getUserData(@CurrentUser user: UserAccount): String = user.toString()

	@GetMapping("/database/{dbName}/check")
	open fun checkDatabaseConnection(@CurrentUser user: UserAccount, @PathVariable("dbName") dbName: String) =
			databaseAccessService.checkConnection(dbName, user)

	@GetMapping("/database/{dbName}/listTables")
	open fun listDatabaseTables(
			@CurrentUser user: UserAccount,
			@PathVariable("dbName") dbName: String,
			@RequestParam("schema") schema: String?,
			@RequestParam("type") types: List<String>?) =
			databaseAccessService.listTables(dbName, schema, user, types?.map { it.toUpperCase() })

	@GetMapping("/database/{dbName}/{schema}/{table}")
	fun previewTable(
			@CurrentUser user: UserAccount,
			@PathVariable("dbName") dbName: String,
			@PathVariable("schema") schema: String = "public",
			@PathVariable("table") tableName: String,
			@RequestParam(name = "limit", defaultValue = "10") limit: Int) =
			databaseAccessService.previewTable(dbName, schema, tableName, user, limit)
}