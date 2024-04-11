package no.uio.ifi.in2000.team18.airborn.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "builtin_airport")
data class BuiltinAirport(
    @PrimaryKey val icao: String,
    val name: String,
    val lat: Double,
    val lon: Double,
)