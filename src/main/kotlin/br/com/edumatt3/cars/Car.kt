package br.com.edumatt3.cars

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Car(
    @field:NotBlank
    @Column(nullable = false)
    val model: String,

    @field:NotBlank
    @Column(nullable = false)
    val licensePlate: String,
) {

    @Id
    @GeneratedValue
    var id: Long? = null
}
