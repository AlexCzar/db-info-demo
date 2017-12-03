package io.czar.dbinfodemo.services

import io.czar.dbinfodemo.model.DbInfo
import io.czar.dbinfodemo.model.DbTableInfo
import io.czar.dbinfodemo.model.UserAccount


interface SqlDatabaseAccessService {
	fun checkConnection(dbName: String, user: UserAccount): String
	fun getDbInformation(dbName: String, user: UserAccount): DbInfo
	fun listTables(dbName: String, schema: String? = null, user: UserAccount, types: List<String>?): List<DbTableInfo>
	fun getTableInfo(dbName: String, schema: String, tableName: String, user: UserAccount): DbTableInfo
}