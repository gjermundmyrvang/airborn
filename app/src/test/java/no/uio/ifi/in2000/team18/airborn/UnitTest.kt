package no.uio.ifi.in2000.team18.airborn

import junit.framework.TestCase.assertEquals
import no.uio.ifi.in2000.team18.airborn.model.Humidity
import no.uio.ifi.in2000.team18.airborn.model.celcius
import no.uio.ifi.in2000.team18.airborn.model.degrees
import no.uio.ifi.in2000.team18.airborn.model.hpa
import no.uio.ifi.in2000.team18.airborn.model.mps
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
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
        val celsius = 20.5.celcius
        assertEquals("20.5 ℃", "$celsius")
    }

    @Test
    fun testDirectionInDegrees() {
        val direction = 45.0.degrees
        assertEquals("45.0 degrees", "$direction")
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
}