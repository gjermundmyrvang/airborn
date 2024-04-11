package no.uio.ifi.in2000.team18.airborn.data.dao

import androidx.room.Dao
import androidx.room.Query
import no.uio.ifi.in2000.team18.airborn.data.entity.BuiltinAirport

@Dao
interface BuiltinAirportDao {
    @Query("SELECT * FROM builtin_airport WHERE icao LIKE '%' || :q || '%' OR name LIKE '%' || :q || '%'")
    fun search(q: String): List<BuiltinAirport>
}