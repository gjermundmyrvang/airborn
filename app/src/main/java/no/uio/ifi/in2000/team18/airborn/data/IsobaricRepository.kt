package no.uio.ifi.in2000.team18.airborn.data

import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricLayer
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.pow

class IsobaricRepository @Inject constructor(
    /** TODO: connect to relevant DataSources,
     * probably locationForecast and grib
     */
) {
    // constants for height calculation
    private val k: Double = 1 / 5.25579 // exponent
    private val l: Double = 0.0065 // temperature lapse rate (K/m)

    fun getIsobaricData(position: Position, time: LocalDateTime): IsobaricData {
        // TODO: fetch isobaricData from Datasource
        val isobaricData = IsobaricData( // example
            Position(60.81, 11.06), LocalDateTime.now(), // TODO: should time be nullable
            listOf(
                IsobaricLayer(850.0, -0.83, 199.11, 3.51),
                IsobaricLayer(800.0, -8.36, 227.14, 9.35),
                IsobaricLayer(750.0, -12.52, 241.95, 9.85)
            )
        )

        val data: List<IsobaricLayer> = isobaricData.data.map { layer ->
            IsobaricLayer(
                layer.pressure,
                layer.temperature,
                layer.windFromDirection,
                layer.windSpeed,
                height = calculateHeight(layer) // TODO: better refTemperature and pressureLevelZero
            )
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
    ) = layer.height ?: ( // calculate height
            refTemperature * (1 - (layer.pressure / pressureLevelZero).pow(k)) / l)
}