package io.czar.dbinfodemo.model

import org.springframework.data.jpa.repository.JpaRepository


interface PostgreSettingsRepository : JpaRepository<PostgreSettings, Long>