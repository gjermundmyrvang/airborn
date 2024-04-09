package no.uio.ifi.in2000.team18.airborn.model

import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime


data class LocationData(
    val properties: Properties
)

data class WeatherDay(
    val date: DateTime,
    val weather: List<WeatherHour>
)

data class WeatherHour(
    val time: String,
    val weatherDetails: WeatherDetails,
    val nextOneHour: NextHourDetails? = null,
    val nextSixHour: NextHourDetails? = null,
    val nextTwelweHour: NextHourDetails? = null,
)

data class NextHourDetails(
    val symbol_code: String,
    val icon: Int,
    val chanceOfRain: Double?,
)

data class Meta(
    val updated_at: String,
    val units: Map<String, String>
)

data class Properties(
    val meta: Meta,
    val timeseries: List<TimeSeries>
)

data class Summary(
    val symbol_code: String
)

data class SummaryData(
    val summary: Summary,
    val details: Map<String, Double>
)

data class TimeSeries(
    val time: String,
    val data: TimeSeriesData
)

data class TimeSeriesData(
    val instant: InstantData,
    val next_12_hours: SummaryData? = null,
    val next_1_hours: SummaryData? = null,
    val next_6_hours: SummaryData? = null
)

data class InstantData(
    val details: Details
)

data class WeatherDetails(
    val airPressureSeaLevel: Hpa,
    val airTemperature: Temperature,
    val cloudFraction: CloudFraction,
    val humidity: Humidity,
    val windDirection: DirectionInDegrees,
    val windSpeed: Speed,
    val dewPointTemperature: Temperature,
    val fogAreaFraction: FogAreaFraction,
    val uvIndex: UvIndex,
)

data class Details(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val air_temperature_max: Double,
    val air_temperature_min: Double,
    val cloud_area_fraction: Double,
    val cloud_area_fraction_high: Double,
    val cloud_area_fraction_low: Double,
    val cloud_area_fraction_medium: Double,
    val dew_point_temperature: Double,
    val fog_area_fraction: Double,
    val relative_humidity: Double,
    val ultraviolet_index_clear_sky: Double,
    val wind_from_direction: Double,
    val wind_speed: Double
)

enum class SymbolCode {
    ClearskyDay,
    ClearskyNight,
    Cloudy,
    FairDay,
    FairNight,
    Lightrain,
    Lightsleet,
    LightsleetshowersDay,
    Lightsnow,
    PartlycloudyDay,
    PartlycloudyNight,
    Rain,
    Sleet,
    Snow
}


