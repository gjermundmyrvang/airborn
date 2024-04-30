package no.uio.ifi.in2000.team18.airborn

import junit.framework.TestCase.assertEquals
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Humidity
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.Temperature
import no.uio.ifi.in2000.team18.airborn.model.degrees
import no.uio.ifi.in2000.team18.airborn.model.format
import no.uio.ifi.in2000.team18.airborn.model.hpa
import no.uio.ifi.in2000.team18.airborn.model.mps
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class UnitTest {
    @Test
    fun testHpa() {
        val pressure = 1013.25.hpa
        assertEquals("1013.25 hPa", "$pressure")
    }

    @Test
    fun testHumidity() {
        val humidity = Humidity(65.5)
        assertEquals("65.5 %", "$humidity")
    }

    @Test
    fun testMetersPerSecond() {
        val speed = 10.2.mps
        assertEquals("10.2 m/s", "$speed")
    }

    @Test
    fun testCelsius() {
        val celsius = Temperature(20.5)
        assertEquals("21 \u2103", "$celsius")
    }

    @Test
    fun testDirectionInDegrees() {
        val direction = 45.0.degrees
        assertEquals("45°", "$direction")
    }

    @Test
    fun testToLocalDateTime() {
        val isoDateTime = "2024-04-04T11:36:16Z"
        val dateTime = DateTime(isoDateTime)

        val expectedDateTime = LocalDateTime.of(2024, 4, 4, 11, 36, 16).atZone(ZoneOffset.UTC)
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

        assertEquals(expectedDateTime, dateTime.toLocalDateTime())
    }

    @Test
    fun testGetDate() {
        val isoDateTime = "2024-04-04T11:36:16Z"
        val dateTime = DateTime(isoDateTime)

        val expectedDate = "04. Apr, 2024"
        val notExpectedDate = "2024-04-04"

        assertEquals(expectedDate, dateTime.date)
        assert(notExpectedDate != dateTime.date)
    }

    @Test
    fun testGetTime() {
        val isoDateTime = "2024-04-04T11:36:16Z"
        val dateTime = DateTime(isoDateTime)

        val expectedTime = "13:36"

        assertEquals(expectedTime, dateTime.time)
    }

    @Test
    fun testWindDirectionFromUVWest() {
        val u = 1.0
        val v = 0.0
        val direction = Direction.fromWindUV(u, v)
        val expectedDirection = Direction.WEST

        assertEquals(direction, expectedDirection)
    }

    @Test
    fun testWindDirectionFromUVSouth() {
        val u = 0.0
        val v = 1.0
        val direction = Direction.fromWindUV(u, v)
        val expectedDirection = Direction.SOUTH

        assertEquals(direction, expectedDirection)
    }

    @Test
    fun bearing_isCorrect() {
        // Arrange
        val start = Position(60.0, 10.0)
        val end = Position(61.0, 9.0)
        // Act
        val result = start.bearingTo(end)
        // Assert
        Assert.assertEquals(Direction(334.0).degrees, result.degrees, 0.5)
    }

    @Test
    fun halfway_isCorrect() {
        // Arrange
        val start = Position(35.0, 45.0)
        val end = Position(35.0, 135.0)
        // Act
        val result: Position = start.halfwayTo(end)
        // Assert
        Assert.assertEquals(45.0, result.latitude, 0.5)
        Assert.assertEquals(90.0, result.longitude, 0.5)
    }

    @Test
    fun distance_isCorrect() {
        // Arrange
        val start = Position(35.0, 45.0)
        val end = Position(35.0, 135.0)
        // Act
        val result: Distance = start.distanceTo(end)
        // Assert
        Assert.assertEquals(7872000.0, result.meters, 500.0)
    }

    @Test
    fun `format double with 3 decimals`() {
        val pi = 3.14159265358979
        assertEquals("3.142", pi.format(3))
    }

    @Test
    fun `format double with negative decimals`() {
        val n = -273.15
        assertEquals("-270", n.format(-1))
    }
}