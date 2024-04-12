package no.uio.ifi.in2000.team18.airborn.data.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team18.airborn.data.dao.BuiltinAirportDao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import javax.inject.Inject
import kotlin.math.absoluteValue

class AirportDataSource @Inject constructor(
    val builtinAirportDao: BuiltinAirportDao
) {
    fun getByIcao(icao: Icao) = airports.find { it.icao == icao }

    suspend fun search(query: String): List<Airport> = withContext(Dispatchers.IO) {
        builtinAirportDao.search(query).map {
            Airport(
                icao = Icao(it.icao),
                name = it.name,
                position = Position(latitude = it.lat, longitude = it.lon)
            )
        }
    }

    /**
     * @param airport Use airport position to get nearby airports based on map coordinates.
     * Use this approximation for distance: 1 unit of latitude = 2 units of longitude.
     * Note that this will look more like a perfect square area for southern part of Norway
     * and less so for areas further north.
     * */
    fun getAirportsNearby(airport: Airport) = airports.filter {
        val pos = airport.position
        it.position != pos && it.position.latitude.minus(pos.latitude).absoluteValue < 1 && it.position.longitude.minus(
            pos.longitude
        ).absoluteValue < 2
    }

    val airports = listOf(
        Airport(
            icao = Icao("ENAL"), name = "Ålesund airport, Vigra", position = Position(62.56, 06.11)
        ),
        Airport(
            icao = Icao("ENAN"), name = "Andøya airport, Andenes", position = Position(69.29, 16.14)
        ),
        Airport(
            icao = Icao("ENAS"),
            name = "Ny-Ålesund airport, Havnerabben",
            position = Position(78.92, 11.87)
        ),
        Airport(icao = Icao("ENAT"), name = "Alta airport", position = Position(69.97, 23.37)),
        Airport(
            icao = Icao("ENBL"),
            name = "Førde airport, Bringeland",
            position = Position(61.39, 05.75)
        ),
        Airport(
            icao = Icao("ENBM"), name = "Voss airport, Bømoen", position = Position(60.63, 06.49)
        ),
        Airport(
            icao = Icao("ENBN"),
            name = "Brønnøysund airport, Brønnøy",
            position = Position(65.46, 12.21)
        ),
        Airport(icao = Icao("ENBO"), name = "Bodø airport", position = Position(67.26, 14.36)),
        Airport(
            icao = Icao("ENBR"),
            name = "Bergen airport, Flesland",
            position = Position(60.29, 05.21)
        ),
        Airport(icao = Icao("ENBS"), name = "Båtsfjord airport", position = Position(70.60, 29.69)),
        Airport(icao = Icao("ENBV"), name = "Berlevåg airport", position = Position(70.87, 29.03)),
        Airport(
            icao = Icao("ENCN"),
            name = "Kristiansand airport, Kjevik",
            position = Position(58.20, 08.08)
        ),
        Airport(
            icao = Icao("ENDI"), name = "Geilo airport, Dagali", position = Position(60.41, 08.51)
        ),
        Airport(icao = Icao("ENDU"), name = "Bardufoss airport", position = Position(69.05, 18.54)),
        Airport(
            icao = Icao("ENEG"),
            name = "Hønefoss airport, Eggemoen",
            position = Position(60.21, 10.32)
        ),
        Airport(
            icao = Icao("ENEV"),
            name = "Harstad/Narvik airport, Evenes",
            position = Position(68.48, 16.67)
        ),
        Airport(
            icao = Icao("ENFG"),
            name = "Fagernes airport, Leirin",
            position = Position(61.01, 09.28)
        ),
        Airport(icao = Icao("ENFL"), name = "Florø airport", position = Position(61.58, 05.02)),
        Airport(
            icao = Icao("ENGK"),
            name = "Arendal airport, Gullknapp",
            position = Position(58.51, 08.70)
        ),
        Airport(
            icao = Icao("ENGM"),
            name = "Oslo airport, Gardermoen",
            position = Position(60.20, 11.08)
        ),
        Airport(
            icao = Icao("ENHA"),
            name = "Hamar airport, Stafsberg",
            position = Position(60.81, 11.06)
        ),
        Airport(
            icao = Icao("ENHD"),
            name = "Haugesund airport, Karmøy",
            position = Position(59.34, 05.21)
        ),
        Airport(
            icao = Icao("ENHF"), name = "Hammerfest airport", position = Position(70.67, 23.66)
        ),
        Airport(icao = Icao("ENHK"), name = "Hasvik airport", position = Position(70.48, 22.13)),
        Airport(icao = Icao("ENHS"), name = "Hokksund airport", position = Position(59.76, 09.91)),
        Airport(
            icao = Icao("ENHV"),
            name = "Honningsvåg airport, Valan",
            position = Position(71.00, 25.98)
        ),
        Airport(
            icao = Icao("ENJB"),
            name = "Tønsberg airport, Jarlsberg",
            position = Position(59.30, 10.36)
        ),
        Airport(
            icao = Icao("ENKB"),
            name = "Kristiansund airport, Kvernberget",
            position = Position(63.11, 07.82)
        ),
        Airport(icao = Icao("ENKJ"), name = "Kjeller airport", position = Position(59.96, 11.03)),
        Airport(
            icao = Icao("ENKL"), name = "Gol airport, Klanten", position = Position(60.79, 09.05)
        ),
        Airport(
            icao = Icao("ENKR"),
            name = "Kirkenes airport, Høybuktmoen",
            position = Position(69.72, 29.88)
        ),
        Airport(
            icao = Icao("ENLI"), name = "Farsund airport, Lista", position = Position(58.10, 06.62)
        ),
        Airport(icao = Icao("ENLK"), name = "Leknes airport", position = Position(68.15, 13.60)),
        Airport(icao = Icao("ENMH"), name = "Mehamn airport", position = Position(71.02, 27.82)),
        Airport(
            icao = Icao("ENML"), name = "Molde airport, Årø", position = Position(62.74, 07.26)
        ),
        Airport(
            icao = Icao("ENNA"), name = "Lakselv airport, Banak", position = Position(70.06, 24.97)
        ),
        Airport(icao = Icao("ENNM"), name = "Namsos airport", position = Position(64.47, 11.57)),
        Airport(icao = Icao("ENNO"), name = "Notodden airport", position = Position(59.56, 09.21)),
        Airport(icao = Icao("ENOL"), name = "Ørland airport", position = Position(63.69, 09.60)),
        Airport(
            icao = Icao("ENOP"),
            name = "Oppdal airport, Fagerhaug",
            position = Position(62.65, 09.85)
        ),
        Airport(
            icao = Icao("ENOV"),
            name = "Ørsta/Volda airport, Hovden AS",
            position = Position(62.18, 06.07)
        ),
        Airport(
            icao = Icao("ENRA"),
            name = "Mo i Rana airport, Røssvoll",
            position = Position(66.36, 14.30)
        ),
        Airport(
            icao = Icao("ENRI"), name = "Ringebu airport, Frya", position = Position(61.54, 10.06)
        ),
        Airport(
            icao = Icao("ENRK"),
            name = "Rakkestad airport, Åstorp",
            position = Position(59.39, 11.34)
        ),
        Airport(
            icao = Icao("ENRM"), name = "Rørvik airport, Ryum", position = Position(64.83, 11.14)
        ),
        Airport(icao = Icao("ENRO"), name = "Røros airport", position = Position(62.57, 11.34)),
        Airport(icao = Icao("ENRS"), name = "Røst airport", position = Position(67.52, 12.10)),
        Airport(icao = Icao("ENRV"), name = "Reinsvoll airport", position = Position(60.67, 10.56)),
        Airport(icao = Icao("ENSA"), name = "Svea airport", position = Position(77.89, 16.72)),
        Airport(
            icao = Icao("ENSB"),
            name = "Svalbard airport, Longyear",
            position = Position(78.24, 15.46)
        ),
        Airport(
            icao = Icao("ENSD"), name = "Sandane airport, Anda", position = Position(61.83, 06.10)
        ),
        Airport(
            icao = Icao("ENSG"),
            name = "Sogndal airport, Haukåsen",
            position = Position(61.15, 07.13)
        ),
        Airport(
            icao = Icao("ENSH"), name = "Svolvær airport, Helle", position = Position(68.24, 14.66)
        ),
        Airport(icao = Icao("ENSI"), name = "Ski airport", position = Position(59.71, 10.83)),
        Airport(
            icao = Icao("ENSK"),
            name = "Stokmarknes airport, Skagen",
            position = Position(68.58, 15.02)
        ),
        Airport(
            icao = Icao("ENSM"),
            name = "Elverum airport, Starmoen",
            position = Position(60.87, 11.67)
        ),
        Airport(
            icao = Icao("ENSN"),
            name = "Skien airport, Geiteryggen",
            position = Position(59.18, 09.56)
        ),
        Airport(
            icao = Icao("ENSO"),
            name = "Stord airport, Sørstokken",
            position = Position(59.79, 05.33)
        ),
        Airport(icao = Icao("ENSR"), name = "Sørkjosen airport", position = Position(69.78, 20.95)),
        Airport(
            icao = Icao("ENSS"), name = "Vardø airport, Svartnes", position = Position(70.35, 31.04)
        ),
        Airport(
            icao = Icao("ENST"),
            name = "Sandnessjøen airport, Stokka",
            position = Position(65.95, 12.47)
        ),
        Airport(
            icao = Icao("ENSU"),
            name = "Sunndalsøra airport, Vinnu",
            position = Position(62.65, 08.66)
        ),
        Airport(
            icao = Icao("ENTC"), name = "Tromsø airport, Langnes", position = Position(69.68, 18.91)
        ),
        Airport(
            icao = Icao("ENTO"),
            name = "Sandefjord airport, Torp",
            position = Position(59.18, 10.25)
        ),
        Airport(icao = Icao("ENTY"), name = "Tynset airport", position = Position(62.25, 10.67)),
        Airport(
            icao = Icao("ENVA"),
            name = "Trondheim airport, Værnes",
            position = Position(63.45, 10.92)
        ),
        Airport(icao = Icao("ENVD"), name = "Vadsø airport", position = Position(70.06, 29.84)),
        Airport(
            icao = Icao("ENZV"), name = "Stavanger airport, Sola", position = Position(58.87, 05.63)
        ),
    )
}