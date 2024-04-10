package no.uio.ifi.in2000.team18.airborn.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
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
    val weatherDetails: Details,
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


data class Details(
    @SerializedName("air_pressure_at_sea_level") @JsonAdapter(PressureAdapter::class) val airPressureAtSeaLevel: Pressure,
    @SerializedName("air_temperature") @JsonAdapter(CelsiusAdapter::class) val airTemperature: Temperature,
    @SerializedName("air_temperature_max") @JsonAdapter(CelsiusAdapter::class) val airTemperatureMax: Temperature,
    @SerializedName("air_temperature_min") @JsonAdapter(CelsiusAdapter::class) val airTemperatureMin: Temperature,
    @SerializedName("cloud_area_fraction") @JsonAdapter(FractionAdapter::class) val cloudAreaFraction: Fraction,
    @SerializedName("cloud_area_fraction_high") @JsonAdapter(FractionAdapter::class) val cloudAreaFractionHigh: Fraction,
    @SerializedName("cloud_area_fraction_low") @JsonAdapter(FractionAdapter::class) val cloudAreaFractionLow: Fraction,
    @SerializedName("cloud_area_fraction_medium") @JsonAdapter(FractionAdapter::class) val cloudAreaFractionMedium: Fraction,
    @SerializedName("dew_point_temperature") @JsonAdapter(CelsiusAdapter::class) val dewPointTemperature: Temperature,
    @SerializedName("fog_area_fraction") @JsonAdapter(FractionAdapter::class) val fogAreaFraction: Fraction,
    @SerializedName("relative_humidity") @JsonAdapter(HumidityAdapter::class) val relativeHumidity: Humidity,
    @SerializedName("ultraviolet_index_clear_sky") @JsonAdapter(UvAdapter::class) val ultravioletIndexClearSky: UvIndex,
    @SerializedName("wind_from_direction") @JsonAdapter(DirectionAdapter::class) val windFromDirection: Direction,
    @SerializedName("wind_speed") @JsonAdapter(MpsAdapter::class) val windSpeed: Speed
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


