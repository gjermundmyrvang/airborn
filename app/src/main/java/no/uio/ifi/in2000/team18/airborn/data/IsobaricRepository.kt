package no.uio.ifi.in2000.team18.airborn.data

import android.util.Log
import no.uio.ifi.in2000.team18.airborn.model.flightBrief.Position
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricLayer
import ucar.nc2.dt.GridDatatype
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.pow


const val TEMPERATURE_LAPSE_RATE: Double = 0.0065 // in (K/m)
const val PRESSURE_CALCULATION_EXPONENT: Double = 1 / 5.25579

class IsobaricRepository @Inject constructor(
    private val gribDataSource: GribDataSource
) {
    suspend fun getIsobaricData(position: Position, time: LocalDateTime): IsobaricData {
        val gribFiles = gribDataSource.availableGribFiles()
        val pressureTemperaturePairs = gribDataSource.useGribFile(gribFiles.last()) { dataset ->
            val windU = dataset.grids.find { it.shortName == "u-component_of_wind_isobaric" }!!
            val windV = dataset.grids.find { it.shortName == "v-component_of_wind_isobaric" }!!
            val temperature = dataset.grids.find { it.shortName == "Temperature_isobaric" }!!

            Log.d("grib", "${windU.fullName} ${windV.fullName} ${temperature.fullName}")

            temperature.coordinateSystem.verticalAxis.names.mapIndexed { i, named ->
                Pair((named.name.trim().toInt() / 100), temperature.sampleAtPosition(position, i))
            }
        }

        val data: List<IsobaricLayer> = pressureTemperaturePairs.map { pair ->
            val layer = IsobaricLayer(
                pressure = pair.first.toDouble(),
                temperature = pair.second.toDouble(),
                height = null // TODO: better refTemperature and pressureLevelZero
            )
            layer.height = calculateHeight(layer)
            layer
        }
        return IsobaricData(position, time, data)
    }

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