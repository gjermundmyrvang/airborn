package no.uio.ifi.in2000.team18.airborn.ui.localforecast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.Details
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Fraction
import no.uio.ifi.in2000.team18.airborn.model.Humidity
import no.uio.ifi.in2000.team18.airborn.model.NextHourDetails
import no.uio.ifi.in2000.team18.airborn.model.Pressure
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.Temperature
import no.uio.ifi.in2000.team18.airborn.model.UvIndex
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.celsius
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon
import no.uio.ifi.in2000.team18.airborn.ui.common.toSuccess
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.LazyCollapsible
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTheme
import kotlin.random.Random


@Composable
fun WeatherSection(state: LoadingState<List<WeatherDay>>, initWeather: () -> Unit) =
    LazyCollapsible(
        header = "Weather", value = state, onExpand = initWeather
    ) { weather ->
        var selectedDay by rememberSaveable { mutableIntStateOf(0) }
        var selectedHour by rememberSaveable { mutableIntStateOf(0) }
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherNowSection(
                weatherDay = weather[selectedDay],
                today = weather[selectedDay] == weather.first(),
                weatherHour = weather[selectedDay].weather[selectedHour]
            )
            WeatherTodaySection(
                weatherDay = weather[selectedDay], weather[selectedDay].weather[selectedHour]
            ) { hour ->
                selectedHour = hour
            }
            WeatherWeekSection(weatherDays = weather) { day ->
                selectedHour = 0
                selectedDay = day
            }
        }
    }

@Composable
fun WeatherWeekSection(
    weatherDays: List<WeatherDay>, onDaySelected: (Int) -> Unit
) {
    var selectedDay by rememberSaveable {
        mutableIntStateOf(0)
    }
    LazyRow(
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            Spacer(modifier = Modifier.width(10.dp))
        }
        itemsIndexed(weatherDays) { i, day ->
            if (i == 0) {
                WeatherDayCard(
                    weatherDay = day, selected = weatherDays[selectedDay], today = true
                ) {
                    selectedDay = 0
                    onDaySelected(i)
                }
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                WeatherDayCard(
                    weatherDay = day,
                    selected = weatherDays[selectedDay],
                ) {
                    selectedDay = i
                    onDaySelected(i)
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun WeatherDayCard(
    weatherDay: WeatherDay,
    selected: WeatherDay,
    today: Boolean = false,
    onDaySelected: (WeatherDay) -> Unit
) {
    val weatherHours = weatherDay.weather
    val hourNow = weatherHours.first()
    val highestTemp =
        weatherHours.maxByOrNull { it.weatherDetails.airTemperature.celsius }!!.weatherDetails.airTemperature.celsius
    val lowestTemp =
        weatherHours.minByOrNull { it.weatherDetails.airTemperature.celsius }!!.weatherDetails.airTemperature.celsius
    val isSelected = selected == weatherDay
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.outline
    val displayHour =
        if (today) hourNow.nextOneHour else hourNow.nextTwelweHour  // if today we want to show current weather, but for the rest of the week we want a overview

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp, color = borderColor
        ),
        modifier = Modifier
            .width(120.dp)
            .padding(top = 5.dp),
        onClick = { onDaySelected(weatherDay) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (today) "Today" else weatherDay.date,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(
                    id = displayHour?.icon ?: hourNow.nextOneHour?.icon ?: hourNow.nextSixHour?.icon
                    ?: hourNow.nextTwelweHour?.icon ?: R.drawable.image_not_availeable
                ),
                contentDescription = displayHour?.symbol_code ?: hourNow.nextOneHour?.symbol_code
                ?: hourNow.nextSixHour?.symbol_code
                ?: hourNow.nextTwelweHour?.symbol_code ?: "WeatherIcon"
            )
            Text(
                text = "${highestTemp.celsius} / ${lowestTemp.celsius}",
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
fun WeatherTodaySection(
    weatherDay: WeatherDay, selectedHour: WeatherHour, onHourSelected: (Int) -> Unit
) {
    val weatherHours = weatherDay.weather
    Box {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            item {
                Spacer(modifier = Modifier.width(10.dp))
            }
            itemsIndexed(weatherHours) { i, hour ->
                WeatherHourColumn(weatherHour = hour, selectedHour = selectedHour) {
                    onHourSelected(i)
                }
            }
            item {
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
fun WeatherHourColumn(weatherHour: WeatherHour, selectedHour: WeatherHour, onClick: () -> Unit) {
    val nextHourData =
        weatherHour.nextOneHour ?: weatherHour.nextSixHour ?: weatherHour.nextTwelweHour
    val precipitationAmount = nextHourData?.precipitation_amount
    val isSelected = weatherHour == selectedHour
    Column(
        modifier = Modifier
            .padding(5.dp)
            .width(IntrinsicSize.Min)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = weatherHour.time)
        if (precipitationAmount != null && precipitationAmount > 0.0) {
            Text(
                text = "$precipitationAmount mm",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            Text(text = "")
        }
        Image(
            modifier = Modifier.size(50.dp),
            painter = painterResource(
                id = weatherHour.nextOneHour?.icon ?: weatherHour.nextSixHour?.icon
                ?: weatherHour.nextTwelweHour?.icon ?: R.drawable.image_not_availeable
            ),
            contentDescription = weatherHour.nextOneHour?.symbol_code
                ?: weatherHour.nextSixHour?.symbol_code ?: weatherHour.nextTwelweHour?.symbol_code
                ?: "Weathericon"
        )
        Text(
            text = "${weatherHour.weatherDetails.airTemperature}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Box(
            modifier = Modifier
                .height(5.dp)
                .fillMaxWidth()
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent,
                    shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
                )
        )
    }
}

@Composable
fun WeatherNowSection(weatherDay: WeatherDay, today: Boolean, weatherHour: WeatherHour) {
    val nextHours = weatherHour.nextOneHour ?: weatherHour.nextSixHour ?: weatherHour.nextTwelweHour
    val displayHour = weatherHour.nextOneHour ?: weatherHour.nextSixHour
    ?: weatherHour.nextTwelweHour
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color(0x801D1D1D), RoundedCornerShape(5.dp))
            .clip(RoundedCornerShape(5.dp)),
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row {
                Column {
                    Text(
                        text = if (today) "Today" else weatherDay.date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = weatherHour.time, fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                    Text(
                        text = "${weatherHour.weatherDetails.airTemperature}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    modifier = Modifier.size(80.dp), painter = painterResource(
                        id = displayHour?.icon ?: R.drawable.image_not_availeable
                    ), contentDescription = displayHour?.symbol_code ?: "WeatherIcon"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            WindCard(
                windSpeed = weatherHour.weatherDetails.windSpeed,
                fromDegrees = weatherHour.weatherDetails.windFromDirection.degrees
            )
        }
        Spacer(modifier = Modifier.width(IntrinsicSize.Max))
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            if (nextHours != null) {
                Text(
                    text = nextHours.symbol_code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Row {
                Text("Relative Humidity: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.relativeHumidity}", fontSize = 12.sp)
            }
            Row {
                Text("Pressure: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.airPressureAtSeaLevel}", fontSize = 12.sp)
            }
            weatherHour.weatherDetails.fogAreaFraction?.let {
                Row {
                    Text("Fog area: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(it.toString(), fontSize = 12.sp)
                }
            }
            Row {
                Text("Dewpoint temp: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.dewPointTemperature}", fontSize = 12.sp)
            }
            weatherHour.weatherDetails.ultravioletIndexClearSky?.let {
                Row {
                    Text("UV: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(it.toString(), fontSize = 12.sp)
                }
            }
            Row {
                Text("Cloud fraction: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.cloudAreaFraction}", fontSize = 12.sp)
            }
            Row {
                Spacer(Modifier.width(10.dp))
                Text("high: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.cloudAreaFractionHigh}", fontSize = 12.sp)
            }
            Row {
                Spacer(Modifier.width(10.dp))
                Text("medium: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.cloudAreaFractionMedium}", fontSize = 12.sp)
            }
            Row {
                Spacer(Modifier.width(10.dp))
                Text("low: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${weatherHour.weatherDetails.cloudAreaFractionLow}", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun WindCard(windSpeed: Speed, fromDegrees: Double) {
    val fromDirection = Direction(fromDegrees)
    val direction = when {
        fromDegrees < 90.0 -> "NE"
        fromDegrees < 180.0 -> "SE"
        fromDegrees < 270.0 -> "SW"
        fromDegrees < 360.0 -> "NW"
        else -> "NE"
    }
    OutlinedCard(
        Modifier.width(IntrinsicSize.Max),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(
                    text = windSpeed.formatAsKnots(), fontWeight = FontWeight.Bold, fontSize = 22.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.air_icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.secondary
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                RotatableArrowIcon(direction = fromDirection)
                Text(fromDirection.toString() + direction)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestWindCard() {
    AirbornTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            WindCard(windSpeed = Speed(25.89), fromDegrees = 228.43)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestWeatherSection() {
    val hour = WeatherHour(
        time = "12:00", weatherDetails = Details(
            airPressureAtSeaLevel = Pressure(Random.nextDouble(950.0, 1050.0)),
            airTemperature = Temperature(Random.nextDouble(-10.0, 30.0)),
            airTemperatureMax = Temperature(Random.nextDouble(-5.0, 35.0)),
            airTemperatureMin = Temperature(Random.nextDouble(-15.0, 25.0)),
            cloudAreaFraction = Fraction(Random.nextDouble(0.0, 1.0)),
            cloudAreaFractionHigh = Fraction(Random.nextDouble(0.0, 0.5)),
            cloudAreaFractionLow = Fraction(Random.nextDouble(0.0, 0.3)),
            cloudAreaFractionMedium = Fraction(Random.nextDouble(0.0, 0.7)),
            dewPointTemperature = Temperature(Random.nextDouble(-15.0, 25.0)),
            fogAreaFraction = Fraction(Random.nextDouble(0.0, 0.1)),
            relativeHumidity = Humidity(Random.nextDouble(0.0, 100.9)),
            ultravioletIndexClearSky = UvIndex(Random.nextDouble(0.0, 10.0)),
            windFromDirection = Direction(Random.nextDouble(0.0, 360.0)),
            windSpeed = Speed(Random.nextDouble(0.0, 20.0))
        ), nextOneHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            precipitation_amount = 22.98
        ), nextSixHour = NextHourDetails(
            symbol_code = "Sunny", icon = R.drawable.clearsky_day, precipitation_amount = null
        ), nextTwelweHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            precipitation_amount = 22.98
        )
    )
    val day = WeatherDay(
        date = "11. may", weather = listOf(
            hour, hour, hour, hour
        )
    )
    val weatherdays = listOf(
        day, day, day, day
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        WeatherSection(state = weatherdays.toSuccess(), initWeather = {})
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestWeatherNowSection() {
    val hour = WeatherHour(
        time = "12:00", weatherDetails = Details(
            airPressureAtSeaLevel = Pressure(Random.nextDouble(950.0, 1050.0)),
            airTemperature = Temperature(Random.nextDouble(-10.0, 30.0)),
            airTemperatureMax = Temperature(Random.nextDouble(-5.0, 35.0)),
            airTemperatureMin = Temperature(Random.nextDouble(-15.0, 25.0)),
            cloudAreaFraction = Fraction(Random.nextDouble(0.0, 1.0)),
            cloudAreaFractionHigh = Fraction(Random.nextDouble(0.0, 0.5)),
            cloudAreaFractionLow = Fraction(Random.nextDouble(0.0, 0.3)),
            cloudAreaFractionMedium = Fraction(Random.nextDouble(0.0, 0.7)),
            dewPointTemperature = Temperature(Random.nextDouble(-15.0, 25.0)),
            fogAreaFraction = Fraction(Random.nextDouble(0.0, 0.1)),
            relativeHumidity = Humidity(Random.nextDouble(0.0, 100.9)),
            ultravioletIndexClearSky = UvIndex(Random.nextDouble(0.0, 10.0)),
            windFromDirection = Direction(Random.nextDouble(0.0, 360.0)),
            windSpeed = Speed(Random.nextDouble(0.0, 20.0))
        ), nextOneHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            precipitation_amount = 22.98
        ), nextSixHour = NextHourDetails(
            symbol_code = "Sunny", icon = R.drawable.clearsky_day, precipitation_amount = null
        ), nextTwelweHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            precipitation_amount = 22.98
        )
    )
    val day = WeatherDay(
        date = "11. May", weather = listOf(
            hour, hour, hour
        )
    )
    WeatherNowSection(weatherDay = day, true, hour)
}
