package io.czar.dbinfodemo.services

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.czar.dbinfodemo.model.*
import mu.KLogging
import org.springframework.jdbc.core.JdbcTemplate
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
					val primaryKeys = connection.metaData.getPrimaryKeys(null, schema, name)
					while (primaryKeys.next()) {
						logger.info {
							(primaryKeys.getString("COLUMN_NAME") + "===" + primaryKeys.getString("PK_NAME"))
						}
					}
					val importedKeys = connection.metaData.getImportedKeys(null, schema, name)
					while (importedKeys.next()) {
						logger.info {
							"${importedKeys.getString("PKTABLE_NAME")}.${importedKeys.getString("PKCOLUMN_NAME")}→${importedKeys.getString(
								"FKTABLE_NAME"
							)}.${importedKeys.getString("FKCOLUMN_NAME")}"
						}
					}
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

	override fun previewTable(
		dbName: String,
		schema: String,
		tableName: String,
		user: UserAccount,
		limit: Int
	): TablePreview =
		JdbcTemplate(buildDataSource(dbName, user))
			.query("SELECT * FROM $schema.$tableName LIMIT $limit", previewExtractor)
				?: TablePreview.EMPTY

	val previewExtractor = ResultSetExtractor { resultSet ->
		val columnCount = resultSet.metaData.columnCount
		val columns: List<String> = (1..columnCount).map(resultSet.metaData::getColumnLabel)
		val rows: List<List<Any?>> = generateSequence {
			if (resultSet.next()) {
				columns.map(resultSet::getObject)
			} else null
		}.toList()
		TablePreview(columns, rows)
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
		return user.configurations.find { it.name == dbName }
				?: throw IllegalArgumentException("Database $dbName not configured.")
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

