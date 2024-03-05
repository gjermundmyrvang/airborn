package no.uio.ifi.in2000.team18.airborn

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team18.airborn.data.SigchartDataSource
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        runBlocking {
            val data = SigchartDataSource().fetchSigcharts()
            println(data)
        }


    }
}