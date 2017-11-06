package io.czar.dbinfodemo.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "postgre_settings")
data class PostgreSettings(
		@Id
		@GeneratedValue
		override val id: Long? = null,
		var userId: Long,
		var name: String,
		var host: String,
		var database: String,
		var user: String,
		var password: String,
		var url: String? = null
) : BaseEntity