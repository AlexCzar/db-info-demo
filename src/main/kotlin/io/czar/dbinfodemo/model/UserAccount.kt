package io.czar.dbinfodemo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "user_accounts")
class UserAccount(
		@Id
		@GeneratedValue
		override val id: Long? = null,
		@Column(unique = true, nullable = false, updatable = false)
		val username: String,
		var password: String,
		var enabled: Boolean = true,
		@OneToMany(fetch = FetchType.LAZY, mappedBy = "userId")
		val configurations: MutableSet<PostgreSettings> = mutableSetOf()
) : BaseEntity {

	@Column(unique = true)
	@JsonIgnore
	private lateinit var usernameLowerCased: String

	@PrePersist
	@PreUpdate
	private fun massage() {
		usernameLowerCased = username.toLowerCase()
	}
}

