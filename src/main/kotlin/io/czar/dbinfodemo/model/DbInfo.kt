package io.czar.dbinfodemo.model


data class DbInfo(
		val name: String,
		val schemas: List<DbSchemaInfo>
)

data class DbSchemaInfo(
		val name: String,
		val tables: List<DbTableInfo>
)

data class DbTableInfo(
		val name: String,
		val type: String,
		val isInsertable: Boolean,
		var columns: List<DbColumnInfo> = emptyList()
)

data class DbColumnInfo(
		val name: String,
		val type: String
)