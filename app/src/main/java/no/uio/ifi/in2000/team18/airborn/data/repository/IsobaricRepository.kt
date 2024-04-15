package no.uio.ifi.in2000.team18.airborn.data.repository

import android.util.Log
import no.uio.ifi.in2000.team18.airborn.data.datasource.GribDataSource
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricLayer
import no.uio.ifi.in2000.team18.airborn.model.mps
import ucar.nc2.dt.GridDatatype
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt


const val TEMPERATURE_LAPSE_RATE: Double = 0.0065 // in (K/m)
const val PRESSURE_CALCULATION_EXPONENT: Double = 1 / 5.25579

class IsobaricRepository @Inject constructor(
    private val gribDataSource: GribDataSource
) {
    suspend fun getIsobaricData(position: Position, time: LocalDateTime): IsobaricData {
        val gribFiles = gribDataSource.availableGribFiles()
        val windsAloft = gribDataSource.useGribFile(gribFiles.last()) { dataset ->
            val windU = dataset.grids.find { it.shortName == "u-component_of_wind_isobaric" }!!
            val windV = dataset.grids.find { it.shortName == "v-component_of_wind_isobaric" }!!
            val temperature = dataset.grids.find { it.shortName == "Temperature_isobaric" }!!

            Log.d("grib", "${windU.fullName} ${windV.fullName} ${temperature.fullName}")

            temperature.coordinateSystem.verticalAxis.names.mapIndexed { i, named ->
                (named.name.trim().toInt() / 100) to listOf(
                    temperature.sampleAtPosition(position, i).toDouble(),
                    windU.sampleAtPosition(position, i).toDouble(),
                    windV.sampleAtPosition(position, i).toDouble(),
                )
            }.toMap()
        }


        val layers = windsAloft.map { (key, value) ->
            val layer = IsobaricLayer(
                pressure = key.toDouble(), // TODO: as pressure?
                temperature = value[0], // TODO: as temperature?
                uWind = value[1],
                vWind = value[2]
            )
            // TODO: use direction-method, delete obsolete code
            // layer.windFromDirection = calculateWindDirection(layer.uWind, layer.vWind)?.times(1.0)
            layer.windFromDirection = Direction.fromWindUV(layer.uWind, layer.vWind)
            layer.windSpeed =
                calculateWindSpeed(layer.uWind, layer.vWind)
            layer.height = calculateHeight(layer) // TODO: use unit, fix comparison
            layer
        }.filter {
            val h = it.height
            val maxHeight = 15000
            val result = if (h != null) (h <= maxHeight) else false
            result
        }
        return IsobaricData(position, time, layers)
    }

    private fun calculateWindSpeed(uWind: Double, vWind: Double): Speed =
        (sqrt(uWind.pow(2) + vWind.pow(2))).mps

    /**
     * Calculate height of isobaric layer
     *
     * @param refTemperature todo: what is this
     * @param pressureLevelZero the pressure at sea level
     */
    fun calculateHeight(
        layer: IsobaricLayer,
        refTemperature: Double = 288.15,
        pressureLevelZero: Double = 1013.25,
    ) = layer.height ?: (refTemperature * (1 - (layer.pressure / pressureLevelZero).pow(
        PRESSURE_CALCULATION_EXPONENT
    )) / TEMPERATURE_LAPSE_RATE)
}

fun GridDatatype.sampleAtPosition(position: Position, layer: Int, time: Int = 0): Float {
    val i = coordinateSystem.findXYindexFromLatLon(position.latitude, position.longitude, null)
    val arr = readDataSlice(time, layer, i[1], i[0])
    return arr.getFloat(0)
}