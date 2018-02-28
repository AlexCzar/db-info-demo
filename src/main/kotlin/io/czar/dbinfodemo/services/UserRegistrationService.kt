package io.czar.dbinfodemo.services

import io.czar.dbinfodemo.api.UserRegistration
import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserRegistrationService(
	private val passwordEncoder: PasswordEncoder,
	private val userAccountRepository: UserAccountRepository
) {

	@Throws(IllegalArgumentException::class)
	fun register(registration: UserRegistration) {
		userAccountRepository.findByUsernameLowerCased(registration.username.toLowerCase())?.run {
			throw IllegalArgumentException("User with username '$username' already exists.")
		}
		userAccountRepository.save(registration.createUserAccount())
	}

	fun UserRegistration.createUserAccount() = UserAccount(
		username = username,
		password = passwordEncoder.encode(password)
	)

}