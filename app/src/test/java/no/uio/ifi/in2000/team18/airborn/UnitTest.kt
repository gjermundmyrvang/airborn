package no.uio.ifi.in2000.team18.airborn

import junit.framework.TestCase.assertEquals
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.Celsius
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.CloudFraction
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.DirectionInDegrees
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.Hpa
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.Humidity
import no.uio.ifi.in2000.team18.airborn.ui.localforecast.MetersPerSecond
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class UnitTest {
    @Test
    fun testHpa() {
        val hpa = Hpa(1013.25)
        assertEquals("1013.25 hPa", "$hpa")
    }

    @Test
    fun testHumidity() {
        val humidity = Humidity(65.5)
        assertEquals("65.5 %", "$humidity")
    }

    @Test
    fun testMetersPerSecond() {
        val mps = MetersPerSecond(10.2)
        assertEquals("10.2 m/s", "$mps")
    }

    @Test
    fun testCelsius() {
        val celsius = Celsius(20.5)
        assertEquals("20.5 ℃", "$celsius")
    }

    @Test
    fun testDirectionInDegrees() {
        val direction = DirectionInDegrees(45.0)
        assertEquals("45.0 degrees", "$direction")
    }

    @Test
    fun testCloudFraction() {
        val cloudFraction = CloudFraction(0.75)
        assertEquals("0.75 %", "$cloudFraction")
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