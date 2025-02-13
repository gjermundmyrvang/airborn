package no.uio.ifi.in2000.team18.airborn.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team18.airborn.data.dao.BuiltinAirportDao
import no.uio.ifi.in2000.team18.airborn.data.entity.BuiltinAirport

@Database(
    entities = [
        BuiltinAirport::class,
    ],
    version = 2,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val builtinAirportDao: BuiltinAirportDao
}