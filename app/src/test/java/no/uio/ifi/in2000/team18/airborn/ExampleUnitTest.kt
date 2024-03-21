package no.uio.ifi.in2000.team18.airborn

import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team18.airborn.data.SigchartDataSource
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        runBlocking {
            val data = SigchartDataSource(HttpClient()).fetchSigcharts()
            println(data)
        }
    }

    /* @Test
     fun fetchTurbulenceIsCorrcet() {
         runBlocking {
             val resMap = TurbulenceDataSource(HttpClient()).fetchTurbulenceMap()
             println("\n$resMap")
         }
     }

     @Test
     fun fetchTurbulenceCross_section() {
         runBlocking {
             val res = TurbulenceDataSource(HttpClient()).fetchTurbulenceCrossSection()
             println(res)
         }
     }*/

    /*
    @Test
    fun createTurbulenceModuleAndFetch() {
        runBlocking {
            val dataSource = Turbulences().provideTurbulenceDataSource()
            val res = dataSource.fetchTurbulenceCross_section()
            println(res)
        }
    }*/
}