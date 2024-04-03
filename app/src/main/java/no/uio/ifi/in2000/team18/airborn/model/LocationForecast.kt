package no.uio.ifi.in2000.team18.airborn.model


data class LocationData(
    val properties: Properties
)

data class WeatherDay(
    val date: String,
    val weather: List<WeatherHour>
)

data class WeatherHour(
    val hour: Int,
    val weatherDetails: Details,
    val next_12_hours: SummaryData? = null,
    val next_1_hours: SummaryData? = null,
    val next_6_hours: SummaryData? = null,
    val icon_1_hour: Int? = null,
    val icon_6_hour: Int? = null,
    val icon_12_hour: Int? = null,
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

data class Details(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val cloud_area_fraction: Double,
    val relative_humidity: Double,
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


