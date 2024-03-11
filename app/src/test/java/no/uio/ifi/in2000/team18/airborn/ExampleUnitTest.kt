package no.uio.ifi.in2000.team18.airborn

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team18.airborn.data.SigchartDataSource
import no.uio.ifi.in2000.team18.airborn.data.TurbulenceDataSource
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

    @Test
    fun fetchTurbulenceIsCorrcet() {
        runBlocking {
            val resMap = TurbulenceDataSource().fetchTurbulenceMap()
            println("\n$resMap")
        }
    }

    @Test
    fun fetchTurbulenceCross_section() {
        runBlocking {
            val res = TurbulenceDataSource().fetchTurbulenceCross_section()
            println(res)
        }
    }

}