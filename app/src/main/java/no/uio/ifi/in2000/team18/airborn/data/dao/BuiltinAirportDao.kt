package no.uio.ifi.in2000.team18.airborn.data.dao

import androidx.room.Dao
import androidx.room.Query
import no.uio.ifi.in2000.team18.airborn.data.entity.BuiltinAirport

@Dao
interface BuiltinAirportDao {
    @Query(
        """
        SELECT *
        FROM builtin_airport
        WHERE icao LIKE '%' || :q || '%' OR name LIKE '%' || :q || '%'
        """
    )
    fun search(q: String): List<BuiltinAirport>

    @Query("SELECT * FROM builtin_airport WHERE icao = :icao")
    fun getByIcao(icao: String): BuiltinAirport?

    @Query(
        """
        SELECT * 
        FROM builtin_airport 
        ORDER BY POWER(lat - :latitude, 2) + POWER((lon - :longitude)*0.5, 2)
        LIMIT 10;    
        """
    )
    fun getAirportsNearby(latitude: Double, longitude: Double): List<BuiltinAirport>?
}