package io.czar.dbinfodemo.model

import org.springframework.data.jpa.repository.JpaRepository


interface UserAccountRepository : JpaRepository<UserAccount, Long> {
	fun findByUsernameLowerCased(username: String): UserAccount?
}