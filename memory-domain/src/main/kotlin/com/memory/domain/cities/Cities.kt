package com.memory.domain.cities

import com.memory.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
class Cities(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val geonameId: Long,

    val name: String,

    @Column(name = "asciiname", length = 200)
    val asciiName: String,

    @Column(length = 10)
    val countryCode: String,

    val latitude: Double,

    val longitude: Double,

    val population: Long,
) : BaseTimeEntity()