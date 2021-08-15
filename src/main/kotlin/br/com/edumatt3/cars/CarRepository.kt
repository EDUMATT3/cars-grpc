package br.com.edumatt3.cars

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CarRepository : JpaRepository<Car, Long> {
    fun existsByLicensePlate(licensePlate: String?): Boolean
}
