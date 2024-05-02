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

    // This uses a rough estimation by dividing
    // longitude by two and treating the coordinates as cartesian
    @Query(
        """
        WITH airport_dist as (
            SELECT *,
                (lat - :latitude) * (lat - :latitude) + (lon - :longitude)*(lon - :longitude)/4 as distance_squared
            FROM builtin_airport
        )
        SELECT icao, name, lat, lon, is_favourite
        FROM airport_dist 
        ORDER BY distance_squared
        LIMIT :max
        OFFSET 1;
        """
    )
    fun getAirportsNearby(latitude: Double, longitude: Double, max: Int): List<BuiltinAirport>

    @Query("UPDATE builtin_airport SET is_favourite = 1 WHERE icao = :icao")
    fun addFavourite(icao: String)

    @Query("UPDATE builtin_airport SET is_favourite = 0 WHERE icao = :icao")
    fun removeFavourite(icao: String)
}