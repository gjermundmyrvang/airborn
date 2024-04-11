package no.uio.ifi.in2000.team18.airborn.data

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team18.airborn.data.dao.BuiltinAirportDao
import no.uio.ifi.in2000.team18.airborn.data.entity.BuiltinAirport

@Database(
    entities = [
        BuiltinAirport::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val builtinAirportDao: BuiltinAirportDao
}