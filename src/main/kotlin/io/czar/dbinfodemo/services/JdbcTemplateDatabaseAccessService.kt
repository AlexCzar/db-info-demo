package io.czar.dbinfodemo.services

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.czar.dbinfodemo.model.DbTableInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Service
import java.sql.ResultSet


@Service
class JdbcTemplateDatabaseAccessService : SqlDatabaseAccessService {

	override fun checkConnection(dbName: String): String = perform(dbName, "SELECT 1", connectionStatusExtractor)

	fun <T : Any> perform(dbName: String, query: String, resultSetExtractor: ResultSetExtractor<T>): T {
		val dataSource = buildDataSource(dbName)
		val jdbcTemplate = JdbcTemplate(dataSource)
		val result = jdbcTemplate.query(query, resultSetExtractor)
		dataSource.close()
		return checkNotNull(result)
	}

	override fun listTables(dbName: String, schema: String?): List<DbTableInfo> = perform(
			dbName,
			"select * from information_schema.tables${schema?.let {
				" where table_schema like '$schema'"
			} ?: ""}",
			tablesListExtractor)

	private fun buildDataSource(dbName: String) = HikariDataSource(HikariConfig().apply {
		driverClassName = POSTGRESQL_DRIVER
		username = dbName
		password = dbName
		jdbcUrl = "jdbc:postgresql://localhost:5432/$dbName"
		addDataSourceProperty("databaseName", dbName)
	})

	companion object {
		const val POSTGRESQL_DRIVER = "org.postgresql.Driver"
	}

	private val connectionStatusExtractor = ResultSetExtractor { rs: ResultSet ->
		if (rs.next() && rs.getInt(1) == 1) "Ok" else "No connection or some other problem"
	}

	private val tablesListExtractor = ResultSetExtractor { rs: ResultSet ->
		fun String?.yesNoToBoolean() = this?.toLowerCase() == "yes"
		generateSequence {
			if (rs.next()) DbTableInfo(
					name = rs.getString("table_name"),
					type = rs.getString("table_type"),
					isInsertable = rs.getString("is_insertable_into").yesNoToBoolean()
			) else null
		}.toList()
	}
}

