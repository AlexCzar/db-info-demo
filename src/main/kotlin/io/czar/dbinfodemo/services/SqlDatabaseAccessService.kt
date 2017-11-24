package io.czar.dbinfodemo.services

import io.czar.dbinfodemo.model.DbTableInfo


interface SqlDatabaseAccessService {
	fun checkConnection(dbName: String): String
	fun listTables(dbName: String, schema: String? = null): List<DbTableInfo>
}