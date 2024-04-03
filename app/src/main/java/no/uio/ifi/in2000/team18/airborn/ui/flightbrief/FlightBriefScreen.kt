package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Details
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.SigchartParameters
import no.uio.ifi.in2000.team18.airborn.model.Summary
import no.uio.ifi.in2000.team18.airborn.model.SummaryData
import no.uio.ifi.in2000.team18.airborn.model.WeatherDay
import no.uio.ifi.in2000.team18.airborn.model.WeatherHour
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.AirportBrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.FlightBrief
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Metar
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.MetarTaf
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreen()
}

@Composable
fun FlightBriefScreen(viewModel: FlightBriefViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    when (val flightBrief = state.flightBrief) {
        is LoadingState.Loading -> LoadingScreen()
        is LoadingState.Error -> Text("Error", color = Color.Red)
        is LoadingState.Success -> FlightBriefScreenContent(flightBrief = flightBrief.value)
    }
}

@Composable
fun LoadingScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "LOADING...")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlightBriefScreenContent(flightBrief: FlightBrief) = Column {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()
    TabRow(selectedTabIndex = pagerState.currentPage) {
        Tab(selected = pagerState.currentPage == 0,
            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
            text = { Text("Departure") })
        Tab(selected = pagerState.currentPage == 1,
            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
            text = { Text("Arrival") })
        Tab(selected = pagerState.currentPage == 2,
            onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
            text = { Text("Overall") })
    }
    HorizontalPager(state = pagerState, modifier = Modifier.weight(1.0F)) { index ->
        when (index) {
            0 -> DepartureBriefTab(flightBrief.departure)
            1 -> flightBrief.arrival?.let { ArrivalBriefTab(it) }
            2 -> OverallInfoTab(flightBrief = flightBrief)
        }
    }
}

@Composable
fun DepartureBriefTab(airportBrief: AirportBrief) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
        val airport = airportBrief.airport.name
        Column {
            Text(
                text = airport,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 50.sp,
            )
        }
    }
    item {
        Collapsible(header = "Metar/Taf", expanded = true) {
            Column {
                Text(text = "METAR:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestMetar}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "TAF:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestTaf}")
            }
        }
    }
    item {
        Collapsible(header = "Isobaric data") {
            Column(modifier = Modifier.fillMaxWidth()) {
                // data from isobaric layers, includes height TODO: a table or chart would be nice
                Text(text = "${airportBrief.isobaric?.time}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("height", fontWeight = FontWeight.Bold)
                    Text("temperature", fontWeight = FontWeight.Bold)
                    Text("pressure", fontWeight = FontWeight.Bold)
                }
                airportBrief.isobaric?.data?.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("%.0f m".format(it.height))
                        Text("%.1f c".format(it.temperature - 273.15))
                        Text("%.0f hPa".format(it.pressure))
                    }
                }
            }
        }
    }
    item {
        Collapsible(header = "Turbulence") {
            Column {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.map?.last()?.uri)
                        .setHeader("User-Agent", "Team18").crossfade(500).build(),
                    loading = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                strokeWidth = 1.dp
                            )
                        }
                    },
                    contentDescription = "Image of turbulence map"
                )
                HorizontalDivider(
                    modifier = Modifier.padding(all = 5.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.crossSection?.last()?.uri)
                        .setHeader("User-Agent", "Team18").crossfade(500).build(),
                    loading = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                strokeWidth = 1.dp
                            )
                        }
                    },
                    contentDescription = "Image of turbulence cross section"
                )
            }
        }
    }
    item {
        Collapsible(header = "Weather", padding = 0.dp) {
            Weathersection(weather = airportBrief.weather)
        }
    }
}


@Composable
fun ArrivalBriefTab(airportBrief: AirportBrief) = LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
        val airport = airportBrief.airport.name
        Column {
            Text(
                text = airport,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 50.sp,
            )
        }
    }
    item {
        Collapsible(header = "Metar/Taf", expanded = true) {
            Column {
                Text(text = "METAR:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestMetar}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "TAF:", fontWeight = FontWeight.Bold)
                Text(text = "${airportBrief.metarTaf?.latestTaf}")
            }
        }
    }
    item {
        Collapsible(header = "Isobaric data") {
            Column(modifier = Modifier.fillMaxWidth()) {
                // data from isobaric layers, includes height TODO: a table or chart would be nice
                Text(text = "${airportBrief.isobaric?.time}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("height", fontWeight = FontWeight.Bold)
                    Text("temperature", fontWeight = FontWeight.Bold)
                    Text("pressure", fontWeight = FontWeight.Bold)
                }
                airportBrief.isobaric?.data?.forEach {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("%.0f m".format(it.height))
                        Text("%.1f c".format(it.temperature - 273.15))
                        Text("%.0f hPa".format(it.pressure))
                    }
                }
            }
        }
    }
    item {
        Collapsible(header = "Turbulence") {
            Column {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.map?.last()?.uri)
                        .setHeader("User-Agent", "Team18").crossfade(500).build(),
                    loading = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                strokeWidth = 1.dp
                            )
                        }
                    },
                    contentDescription = "Image of ..."
                )
                HorizontalDivider(
                    modifier = Modifier.padding(all = 5.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(airportBrief.turbulence?.crossSection?.last()?.uri)
                        .setHeader("User-Agent", "Team18").crossfade(500).build(),
                    loading = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                strokeWidth = 1.dp
                            )
                        }
                    },
                    contentDescription = "Image of ..."
                )
            }
        }
    }
    item {
        Collapsible(header = "Weather", padding = 0.dp) {
            Weathersection(weather = airportBrief.weather)
        }
    }
}

@Composable
fun Weathersection(weather: List<WeatherDay>) {
    var selectedDay by remember { mutableStateOf(weather.first()) }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherNowSection(weatherDay = selectedDay, today = selectedDay == weather.first())
        WeatherTodaySection(weatherDay = selectedDay)
        WeatherWeekSection(weatherDays = weather) { day ->
            selectedDay = day
        }
    }
}

@Composable
fun WeatherWeekSection(
    weatherDays: List<WeatherDay>,
    onDaySelected: (WeatherDay) -> Unit
) {
    var selectedDay by remember {
        mutableStateOf(weatherDays.first())
    }
    Box {
        LazyRow(
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Spacer(modifier = Modifier.width(10.dp))
            }
            itemsIndexed(weatherDays) { i, day ->
                if (i == 0) {
                    WeatherDayCard(
                        weatherDay = day,
                        selected = selectedDay,
                        today = true
                    ) { daySelected ->
                        selectedDay = daySelected
                        onDaySelected(daySelected)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                } else {
                    WeatherDayCard(
                        weatherDay = day,
                        selected = selectedDay,
                    ) { daySelected ->
                        selectedDay = daySelected
                        onDaySelected(daySelected)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
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
    val hourNow = weatherDay.weather[0] /*TODO find current hour*/
    val summary =
        hourNow.next_12_hours /* TODO if null, check next_6_hours and if null again check next_1_hour*/
    val highestTemp =
        weatherDay.weather.maxByOrNull { it.weatherDetails.air_temperature }!!.weatherDetails.air_temperature
    val lowestTemp =
        weatherDay.weather.minByOrNull { it.weatherDetails.air_temperature }!!.weatherDetails.air_temperature
    val isSelected = selected == weatherDay
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

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
                    id = hourNow.icon_12_hour ?: R.drawable.ic_launcher_foreground
                ) /*TODO implement errorIcon instead of launcher*/,
                contentDescription = summary?.summary?.symbol_code ?: "Weathericon"
            )
            Text(
                text = "$highestTemp\u2103/$lowestTemp\u2103",
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
    val summary = weatherHour.next_1_hours ?: weatherHour.next_6_hours ?: weatherHour.next_12_hours
    val precipitationAmount = summary?.details?.get("precipitation_amount")
    Column(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "${weatherHour.hour}:00")
        if ((precipitationAmount != null) && (precipitationAmount > 1)) {
            Text(
                text = "${precipitationAmount}%",
                fontSize = 16.sp,
                color = Color.Blue,
            )
        } else {
            Text(
                text = " "
            )
        }
        Image(
            modifier = Modifier.size(50.dp),
            painter = painterResource(
                id = weatherHour.icon_1_hour ?: weatherHour.icon_6_hour ?: weatherHour.icon_12_hour
                ?: R.drawable.ic_launcher_foreground
            ),
            contentDescription = summary?.summary?.symbol_code ?: "Weathericon"
        )
        Text(
            text = "${weatherHour.weatherDetails.air_temperature}" + "\u2103", // celsius
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
    }
}

@Composable
fun WeatherNowSection(weatherDay: WeatherDay, today: Boolean) {
    val weatherHour = weatherDay.weather.first() /*TODO find correct hour*/
    val summary = if (today) weatherHour.next_1_hours else weatherHour.next_12_hours
    val icon = if (today) weatherHour.icon_1_hour else weatherHour.icon_12_hour
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
            if (summary != null) {
                Text(
                    text = summary.summary.symbol_code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Text(
                    text = "Rain: ${summary.details["precipitation_amount"]}%", fontSize = 12.sp
                )
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
        next_12_hours = SummaryData(
            summary = Summary(
                symbol_code = "partly_cloudy"
            ),
            details = mapOf(
                "participation_amount" to 13.87
            )
        ),
        next_6_hours = SummaryData(
            summary = Summary(
                symbol_code = "partly_cloudy"
            ),
            details = mapOf(
                "participation_amount" to 13.87
            )
        ),
        next_1_hours = SummaryData(
            summary = Summary(
                symbol_code = "partly_cloudy"
            ),
            details = mapOf(
                "participation_amount" to 13.87
            )
        ),
        icon_6_hour = R.drawable.partlycloudy_day,
        icon_1_hour = R.drawable.partlycloudy_day,
        icon_12_hour = R.drawable.partlycloudy_day,
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
    val day = WeatherDay(
        date = "torsdag 5. april",
        weather = listOf(
            WeatherHour(
                hour = 12,
                weatherDetails = Details(
                    air_pressure_at_sea_level = 1001.98,
                    air_temperature = 18.0,
                    cloud_area_fraction = 46.9,
                    relative_humidity = 65.98,
                    wind_speed = 23.65,
                    wind_from_direction = 236.98
                ),
                next_12_hours = SummaryData(
                    summary = Summary(
                        symbol_code = "partly_cloudy"
                    ),
                    details = mapOf(
                        "participation_amount" to 13.87
                    )
                ),
                next_6_hours = SummaryData(
                    summary = Summary(
                        symbol_code = "partly_cloudy"
                    ),
                    details = mapOf(
                        "participation_amount" to 13.87
                    )
                ),
                next_1_hours = SummaryData(
                    summary = Summary(
                        symbol_code = "partly_cloudy"
                    ),
                    details = mapOf(
                        "participation_amount" to 13.87
                    )
                ),
                icon_6_hour = R.drawable.partlycloudy_day,
                icon_1_hour = R.drawable.partlycloudy_day,
                icon_12_hour = R.drawable.partlycloudy_day,
            )
        )
    )
    WeatherNowSection(weatherDay = day, true)
}


@Composable
fun OverallInfoTab(flightBrief: FlightBrief) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Collapsible(header = "Sigchart") {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(flightBrief.sigchart.uri)
                        .setHeader("User-Agent", "Team18")
                        .crossfade(500)
                        .build(),
                    loading = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                strokeWidth = 1.dp
                            )
                        }
                    },
                    contentDescription = "Image of sigchart. Updated at ${flightBrief.sigchart.updated}"
                )
            }
        }
    }
}


@Composable
fun Collapsible(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    header: String,
    expanded: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    var open by rememberSaveable {
        mutableStateOf(expanded)
    }
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 300, easing = LinearOutSlowInEasing
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = header, fontSize = 22.sp)
            IconButton(onClick = { open = !open }) {
                Icon(
                    imageVector = if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier.size(30.dp),
                    contentDescription = if (open) "Show less" else "Show more"
                )
            }
        }
        if (open) {
            Box(
                modifier = modifier.padding(padding),
                content = content,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LightPreviewFlightBrief() {
    FlightBriefScreenContent(
        flightBrief = FlightBrief(
            departure = AirportBrief(
                airport = Airport(
                    icao = Icao("ENGM"), name = "Gardermoen", Position(0.0, 0.0)
                ),
                metarTaf = MetarTaf(listOf(Metar("Hello")), listOf()),
                turbulence = null,
                isobaric = null,
                weather = listOf()
            ), arrival = null, altArrivals = listOf(), sigchart = Sigchart(
                params = SigchartParameters(area = Area.Norway, time = ""),
                updated = "",
                uri = "",
            )
        )
    )
}
