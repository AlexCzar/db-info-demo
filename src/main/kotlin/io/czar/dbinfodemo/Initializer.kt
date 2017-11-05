package io.czar.dbinfodemo

import io.czar.dbinfodemo.model.UserAccount
import io.czar.dbinfodemo.model.UserAccountRepository
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
@Profile("dev")
class Initializer(val userAccountRepository: UserAccountRepository,
				  val passwordEncoder: PasswordEncoder) {

	@PostConstruct
	@Transactional
	fun init() {
		UserAccount(
				username = "test",
				password = passwordEncoder.encode("test")
		).let { userAccountRepository.save(it) }
	}
}