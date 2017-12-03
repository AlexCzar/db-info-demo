package io.czar.dbinfodemo.services

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.czar.dbinfodemo.model.*
import mu.KLogging
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.ResultSet


@Service
class MetaDataSqlDatabaseAccessService : SqlDatabaseAccessService {

	companion object : KLogging() {
		const val POSTGRESQL_DRIVER = "org.postgresql.Driver"
	}

	override fun checkConnection(dbName: String, user: UserAccount): String = withDataSourceConnection(dbName, user) {
		if (it.isClosed) "Could not connect" else "Connected"
	}

	override fun getDbInformation(dbName: String, user: UserAccount) = buildDataSource(dbName, user).connection.use {
		val schemas: ResultSet = it.metaData.schemas
		val schemaNames = generateSequence {
			if (schemas.next()) schemas.getString(1) else null
		}.toList()
		DbInfo(it.metaData.databaseProductName, schemaNames)
	}

	override fun listTables(dbName: String, schema: String?, user: UserAccount, types: List<String>?) =
			withDataSourceConnection(dbName, user) { connection ->
				val resultSet = connection.metaData.getTables(null, schema, null, types?.toTypedArray())
				generateSequence {
					if (resultSet.next()) {
						val name = resultSet.getString("table_name")
						val currentTableSchema = resultSet.getString("table_schem")
						DbTableInfo(
								name = name,
								type = resultSet.getString("table_type") ?: "",
								schema = currentTableSchema,
								catalog = resultSet.getString("table_cat") ?: "",
								remarks = resultSet.getString("remarks") ?: "",
								columns = connection.metaData.getColumns(null, currentTableSchema, name, null).columnsInfo()
						)
					} else null
				}.toList()
			}

	private fun <T> withDataSourceConnection(dbName: String, user: UserAccount, block: (Connection) -> T) =
			buildDataSource(dbName, user).connection.use(block)

	private fun buildDataSource(dbName: String, user: UserAccount): HikariDataSource {
		val dbConfiguration = getDbConfiguration(dbName, user)
		return HikariDataSource(HikariConfig().apply {
			driverClassName = POSTGRESQL_DRIVER
			username = dbConfiguration.user
			password = dbConfiguration.password
			jdbcUrl = dbConfiguration.buildUrl()
			addDataSourceProperty("databaseName", dbConfiguration.database)
		})
	}

	override fun getTableInfo(dbName: String, schema: String, tableName: String, user: UserAccount): DbTableInfo {
		TODO("Not yet implemented")
	}

	private fun getDbConfiguration(dbName: String, user: UserAccount): PostgreSettings {
		logger.info { user.configurations }
		return user.configurations.find { it.name == dbName } ?: throw IllegalArgumentException("Database $dbName not configured.")
	}

	private val connectionStatusExtractor = ResultSetExtractor { rs: ResultSet ->
		if (rs.next() && rs.getInt(1) == 1) "Ok" else "No connection or some other problem"
	}
}

private fun ResultSet.columnsInfo(): List<DbColumnInfo> = generateSequence {
	if (next()) DbColumnInfo(
			index = getInt("ORDINAL_POSITION"),
			name = getString("COLUMN_NAME"),
			type = getInt("DATA_TYPE"),
			typeName = getString("TYPE_NAME")
	)
	else null
}.toList()

