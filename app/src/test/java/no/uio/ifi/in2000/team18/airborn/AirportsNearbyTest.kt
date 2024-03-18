package no.uio.ifi.in2000.team18.airborn

import no.uio.ifi.in2000.team18.airborn.data.AirportDataSource
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import org.junit.Assert
import org.junit.Test

class AirportsNearbyTest {


    @Test
    fun getAirportsNearbyNotItself() {
        // Arrange
        val dataSource = AirportDataSource()
        val hamar = Airport(
            icao = Icao("ENHA"),
            name = "Hamar airport, Stafsberg",
            position = Position(60.81, 11.06)
        )

        // Act
        val result: List<Airport> = dataSource.getAirportsNearby(hamar)

        // Assert
        Assert.assertNotEquals(
            listOf
                (
                Airport(
                    icao = Icao("ENHA"),
                    name = "Hamar airport, Stafsberg",
                    position = Position(60.81, 11.06)
                )
            ), result
        )
    }

    @Test
    fun getAirportsNearbyCorrectAndNotIncorrectPosition() {
        // Arrange
        val dataSource = AirportDataSource()
        val hamar = Airport(
            icao = Icao("ENHA"),
            name = "Hamar airport, Stafsberg",
            position = Position(60.81, 11.06)
        )

        // Act
        val result: List<Airport> = dataSource.getAirportsNearby(hamar)

        // Assert
        Assert.assertEquals(
            Airport(
                icao = Icao("ENGM"),
                name = "Oslo airport, Gardermoen",
                position = Position(60.20, 11.08)
            ), result.find { it.icao.code == "ENGM" }
        )

        Assert.assertNotEquals(
            Airport(
                icao = Icao("ENBM"),
                name = "Voss airport, Bømoen",
                position = Position(60.63, 06.49)
            ), result.find { it.icao.code == "ENBM" }
        )

    }

    @Test
    fun getAirportsNearbyButNotTooMany() {
        // Arrange
        val dataSource = AirportDataSource()
        val hamar = Airport(
            icao = Icao("ENHA"),
            name = "Hamar airport, Stafsberg",
            position = Position(60.81, 11.06)
        )

        // Act
        val result: List<Airport> = dataSource.getAirportsNearby(hamar)

        // Assert
        Assert.assertTrue(
            result.size < 8
        )
    }

}