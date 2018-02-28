package io.czar.dbinfodemo.model


data class DbInfo(
	val name: String,
	val schemas: List<String>
)

data class DbTableInfo(
	val name: String,
	val type: String,
	val schema: String,
	val catalog: String,
	val remarks: String,
	val columns: List<DbColumnInfo>
)

data class DbColumnInfo(
	val index: Int,
	val name: String,
	val label: String = name,
	val typeName: String,
	val type: Int
)

data class TablePreview(
	val columns: List<String>,
	val rows: List<List<Any?>>
) {
	companion object {
		val EMPTY = TablePreview(emptyList(), emptyList())
	}
}