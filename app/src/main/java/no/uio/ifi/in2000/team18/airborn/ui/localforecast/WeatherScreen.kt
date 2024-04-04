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
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour


@Composable
fun Weathersection(weather: List<WeatherDay>) {
    var selectedDay by rememberSaveable {
        mutableIntStateOf(0)
    }
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherNowSection(
            weatherDay = weather[selectedDay],
            today = weather[selectedDay] == weather.first()
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
    val weatherHours =
        if (today) weatherDay.weather.filter { it.hour >= LocalTime.now().hour } else weatherDay.weather
    val hourNow = weatherHours.first()
    val highestTemp =
        weatherHours.maxByOrNull { it.weatherDetails.air_temperature }!!.weatherDetails.air_temperature
    val lowestTemp =
        weatherHours.minByOrNull { it.weatherDetails.air_temperature }!!.weatherDetails.air_temperature
    val isSelected = selected == weatherDay
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val icon =
        if (today) hourNow.nextOneHour?.icon else hourNow.nextTwelweHour?.icon // if today we want to show current weather, but for the rest of the week we want a overview

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp, color = borderColor
        ),
        modifier = Modifier
            .fillMaxSize()
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
                text = if (today) "today" else weatherDay.date.substring(0, 3) + ".",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(
                    id = icon ?: R.drawable.ic_launcher_foreground
                ) /*TODO implement errorIcon instead of launcher*/,
                contentDescription = hourNow.nextTwelweHour?.symbol_code ?: "Weathericon"
            )
            Text(
                text = "$highestTemp\u2103/$lowestTemp\u2103",
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
fun WeatherTodaySection(weatherDay: WeatherDay, today: Boolean) {
    val weatherHours =
        if (today) weatherDay.weather.filter { it.hour >= LocalTime.now().hour } else weatherDay.weather
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
        Text(text = "${weatherHour.hour}")
        if ((precipitationAmount != null) && (precipitationAmount > 1)) {
            Text(
                text = "${precipitationAmount}%",
                fontSize = 16.sp,
                color = Color.Blue,
            )
        }
        Image(
            modifier = Modifier.size(50.dp),
            painter = painterResource(
                id = weatherHour.nextOneHour?.icon ?: weatherHour.nextSixHour?.icon
                ?: weatherHour.nextTwelweHour?.icon
                ?: R.drawable.ic_launcher_foreground
            ),
            contentDescription = weatherHour.nextOneHour?.symbol_code
                ?: weatherHour.nextSixHour?.symbol_code ?: weatherHour.nextTwelweHour?.symbol_code
                ?: "Weathericon"
        )
        Text(
            text = "${weatherHour.weatherDetails.air_temperature}" + "\u2103", // celsius
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
    }
}

@Composable
fun WeatherNowSection(weatherDay: WeatherDay, today: Boolean) {
    val weatherHour =
        if (today) weatherDay.weather.first { it.hour >= LocalTime.now().hour } else weatherDay.weather.first()
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
            Text(
                text = if (today) "Now" else weatherDay.date,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Row(
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text(
                    text = "${weatherHour.weatherDetails.air_temperature}" + "\u2103", // celsius
                    fontWeight = FontWeight.Bold, fontSize = 22.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    modifier = Modifier.size(80.dp),
                    painter = painterResource(
                        id = icon ?: R.drawable.ic_launcher_foreground
                    ),
                    contentDescription = "WeatherIcon"
                )
            }
        }
        Column {
            if (nextHours != null) {
                Text(
                    text = nextHours.symbol_code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                if (nextHours.chanceOfRain != null) {
                    Text(
                        text = "Rain: ${nextHours.chanceOfRain}%", fontSize = 12.sp
                    )
                }
            }
            Text(
                text = "Wind: ${weatherHour.weatherDetails.wind_speed}m/s from: ${weatherHour.weatherDetails.wind_from_direction}degrees",
                fontSize = 12.sp
            )
            Text(
                text = "Relative Humidity: ${weatherHour.weatherDetails.relative_humidity}%",
                fontSize = 12.sp
            )
            Text(
                text = "Pressure: ${weatherHour.weatherDetails.air_pressure_at_sea_level}hPa",
                fontSize = 12.sp
            )
            Text(
                text = "Cloud fraction: ${weatherHour.weatherDetails.cloud_area_fraction}%",
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestWeatherSection() {
    val hour = WeatherHour(
        hour = 12,
        weatherDetails = Details(
            air_pressure_at_sea_level = 1001.98,
            air_temperature = 18.0,
            cloud_area_fraction = 46.9,
            relative_humidity = 65.98,
            wind_speed = 23.65,
            wind_from_direction = 236.98
        ),
        nextOneHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            chanceOfRain = 22.98
        ),
        nextSixHour = NextHourDetails(
            symbol_code = "Sunny",
            icon = R.drawable.clearsky_day,
            chanceOfRain = null
        ),
        nextTwelweHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            chanceOfRain = 22.98
        )
    )
    val day = WeatherDay(
        date = "torsdag 5. april",
        weather = listOf(
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
        hour = 12,
        weatherDetails = Details(
            air_pressure_at_sea_level = 1001.98,
            air_temperature = 18.0,
            cloud_area_fraction = 46.9,
            relative_humidity = 65.98,
            wind_speed = 23.65,
            wind_from_direction = 236.98
        ),
        nextOneHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            chanceOfRain = 22.98
        ),
        nextSixHour = NextHourDetails(
            symbol_code = "Sunny",
            icon = R.drawable.clearsky_day,
            chanceOfRain = null
        ),
        nextTwelweHour = NextHourDetails(
            symbol_code = "Partly Cloudy",
            icon = R.drawable.partlycloudy_day,
            chanceOfRain = 22.98
        )
    )
    val day = WeatherDay(
        date = "torsdag 5. april",
        weather = listOf(
            hour, hour, hour
        )
    )
    WeatherNowSection(weatherDay = day, true)
}