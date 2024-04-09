package no.uio.ifi.in2000.team18.airborn.ui.localforecast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.Celsius
import no.uio.ifi.in2000.team18.airborn.model.CloudFraction
import no.uio.ifi.in2000.team18.airborn.model.DirectionInDegrees
import no.uio.ifi.in2000.team18.airborn.model.FogAreaFraction
import no.uio.ifi.in2000.team18.airborn.model.Hpa
import no.uio.ifi.in2000.team18.airborn.model.Humidity
import no.uio.ifi.in2000.team18.airborn.model.NextHourDetails
import no.uio.ifi.in2000.team18.airborn.model.Speed
import no.uio.ifi.in2000.team18.airborn.model.UvIndex
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherDetails
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime


@Composable
fun Weathersection(weather: List<WeatherDay>) {
    var selectedDay by rememberSaveable {
        mutableIntStateOf(0)
    }
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherNowSection(
            weatherDay = weather[selectedDay], today = weather[selectedDay] == weather.first()
        )
        WeatherTodaySection(weatherDay = weather[selectedDay])
        WeatherWeekSection(weatherDays = weather) { day ->
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
                    selectedDay = i
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
        weatherHours.maxByOrNull { it.weatherDetails.airTemperature.value }!!.weatherDetails.airTemperature.value
    val lowestTemp =
        weatherHours.minByOrNull { it.weatherDetails.airTemperature.value }!!.weatherDetails.airTemperature.value
    val isSelected = selected == weatherDay
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val icon =
        if (today) hourNow.nextOneHour?.icon else hourNow.nextTwelweHour?.icon  // if today we want to show current weather, but for the rest of the week we want a overview

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
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
                text = if (today) "today" else weatherDay.date.day,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                modifier = Modifier.size(50.dp), painter = painterResource(
                    id = icon ?: R.drawable.image_not_availeable
                ), contentDescription = "Weathericon"
            )
            Text(
                text = "$highestTemp℃/$lowestTemp℃",
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
fun WeatherTodaySection(weatherDay: WeatherDay) {
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
            items(weatherHours) { hour ->
                WeatherHourColumn(weatherHour = hour)
            }
            item {
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@Composable
fun WeatherHourColumn(weatherHour: WeatherHour) {
    val precipitationAmount = weatherHour.nextOneHour?.chanceOfRain
    Column(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = weatherHour.time)
        if ((precipitationAmount != null) && (precipitationAmount > 1)) {
            Text(
                text = "${precipitationAmount}%",
                fontSize = 16.sp,
                color = Color.Blue,
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
    }
}

@Composable
fun WeatherNowSection(weatherDay: WeatherDay, today: Boolean) {
    val weatherHour = weatherDay.weather.first()
    val nextHours = if (today) weatherHour.nextOneHour else weatherHour.nextTwelweHour
    val icon =
        if (today) weatherHour.nextOneHour?.icon else weatherHour.nextTwelweHour?.icon // if today we want to show current weather, but for the rest of the week we want a overview
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row {
                Column {
                    Text(
                        text = if (today) "Now" else weatherDay.date.dayNumberMonth,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
                        id = icon ?: R.drawable.image_not_availeable
                    ), contentDescription = "WeatherIcon"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            WindCard(
                windSpeed = weatherHour.weatherDetails.windSpeed,
                fromDegrees = weatherHour.weatherDetails.windDirection.value
            )
        }
        Column {
            if (nextHours != null) {
                Text(
                    text = nextHours.symbol_code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Text(
                text = "Rain: ${weatherHour.nextTwelweHour?.chanceOfRain} %", fontSize = 12.sp
            )
            Text(
                text = "Relative Humidity: ${weatherHour.weatherDetails.humidity}", fontSize = 12.sp
            )
            Text(
                text = "Pressure: ${weatherHour.weatherDetails.airPressureSeaLevel}",
                fontSize = 12.sp
            )
            Text(
                text = weatherHour.weatherDetails.cloudFraction.toString(), fontSize = 12.sp
            )
            Text(
                text = "Dewpoint temp: ${weatherHour.weatherDetails.dewPointTemperature}",
                fontSize = 12.sp
            )
            Text(text = "Fog area: ${weatherHour.weatherDetails.fogAreaFraction}", fontSize = 12.sp)
            Text(text = "UV: ${weatherHour.weatherDetails.uvIndex}", fontSize = 12.sp)
        }
    }
}

@Composable
fun WindCard(windSpeed: Speed, fromDegrees: Double) {
    val direction = when {
        fromDegrees < 90.0 -> "NE"
        fromDegrees < 180.0 -> "SE"
        fromDegrees < 270.0 -> "SW"
        fromDegrees < 360.0 -> "NW"
        else -> "NE"
    }
    OutlinedCard(
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(text = "$windSpeed", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Image(
                    painter = painterResource(id = R.drawable.air_icon),
                    contentDescription = "airIcon",
                    colorFilter = ColorFilter.tint(
                        Color.Gray
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                RotatableArrowIcon(direction = fromDegrees)
                Text(text = "$fromDegrees $direction")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestWindCard() {
    WindCard(windSpeed = Speed(25.89), fromDegrees = 228.43)
}

@Composable
fun RotatableArrowIcon(
    direction: Double,
    modifier: Modifier = Modifier,
    iconSize: Dp = 25.dp,
    iconColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val arrowIcon: Painter = painterResource(id = R.drawable.arrow_up)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = arrowIcon,
            contentDescription = "Arrow icon",
            modifier = modifier
                .size(iconSize)
                .rotate(direction.toFloat()),
            colorFilter = ColorFilter.tint(iconColor)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestWeatherSection() {
    val hour = WeatherHour(
        time = "12:00", weatherDetails = WeatherDetails(
            airPressureSeaLevel = Hpa(1001.98),
            airTemperature = Celsius(18.0),
            cloudFraction = CloudFraction(46.9, 78.9, 76.6, 80.5),
            humidity = Humidity(65.98),
            windSpeed = Speed(23.65),
            windDirection = DirectionInDegrees(236.98),
            dewPointTemperature = Celsius(23.9),
            fogAreaFraction = FogAreaFraction(89.9),
            uvIndex = UvIndex(2.0)
        ), nextOneHour = NextHourDetails(
            symbol_code = "Partly Cloudy", icon = R.drawable.partlycloudy_day, chanceOfRain = 22.98
        ), nextSixHour = NextHourDetails(
            symbol_code = "Sunny", icon = R.drawable.clearsky_day, chanceOfRain = null
        ), nextTwelweHour = NextHourDetails(
            symbol_code = "Partly Cloudy", icon = R.drawable.partlycloudy_day, chanceOfRain = 22.98
        )
    )
    val day = WeatherDay(
        date = DateTime("2024-04-09T09:44:36Z"), weather = listOf(
            hour, hour, hour, hour
        )
    )
    val weatherdays = listOf(
        day, day, day, day
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Weathersection(weather = weatherdays)
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestWeatherNowSection() {
    val hour = WeatherHour(
        time = "12:00", weatherDetails = WeatherDetails(
            airPressureSeaLevel = Hpa(1001.98),
            airTemperature = Celsius(18.0),
            cloudFraction = CloudFraction(46.9, 78.9, 76.6, 80.5),
            humidity = Humidity(65.98),
            windSpeed = Speed(23.65),
            windDirection = DirectionInDegrees(236.98),
            dewPointTemperature = Celsius(23.9),
            fogAreaFraction = FogAreaFraction(89.9),
            uvIndex = UvIndex(2.0)
        ), nextOneHour = NextHourDetails(
            symbol_code = "Partly Cloudy", icon = R.drawable.partlycloudy_day, chanceOfRain = 22.98
        ), nextSixHour = NextHourDetails(
            symbol_code = "Sunny", icon = R.drawable.clearsky_day, chanceOfRain = null
        ), nextTwelweHour = NextHourDetails(
            symbol_code = "Partly Cloudy", icon = R.drawable.partlycloudy_day, chanceOfRain = 22.98
        )
    )
    val day = WeatherDay(
        date = DateTime("2024-04-09T09:44:36Z"), weather = listOf(
            hour, hour, hour
        )
    )
    WeatherNowSection(weatherDay = day, true)
}
