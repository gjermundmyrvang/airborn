package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import alexmaryin.metarkt.MetarParser
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import kotlinx.datetime.format
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.model.SigchartParameters
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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.floor

@Preview(showSystemUi = true)
@Composable
fun TestFlightBrief() {
    FlightBriefScreen()
}

@Composable
fun FlightBriefScreen(viewModel: FlightBriefViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    when (val flightBrief = state.flightBrief) {
        is LoadingState.Loading -> {
            LoadingScreen()
        }

        is LoadingState.Error -> Text("Error", color = Color.Red)
        is LoadingState.Success -> FlightBreifScreenContent(flightBrief = flightBrief.value)
    }
}

@Composable
fun LoadingScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(text = "LOADING...")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlightBreifScreenContent(flightBrief: FlightBrief) = Column {
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
        val clipboardManager = LocalClipboardManager.current
        Collapsible(header = "Metar/Taf", expanded = true) {
            Column {
                MetarTaf(metarTaf = airportBrief.metarTaf)
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
                DisplayTurbulence(airportBrief = airportBrief)

            }
        }
    }
    item {
        Collapsible(header = "Weather") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDayCard(
                    weatherDay = airportBrief.weather[0]
                )
            }
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
        Collapsible(header = "Metar/Taf", expanded = false) {
            Column {
                MetarTaf(metarTaf = airportBrief.metarTaf)
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
                DisplayTurbulence(airportBrief = airportBrief)
            }
        }
    }
    item {
        Collapsible(header = "Weather") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDayCard(weatherDay = airportBrief.weather[0])
            }
        }
    }
}

@Composable
fun OverallInfoTab(flightBrief: FlightBrief) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Collapsible(header = "Sigchart") {
                val zoomState = rememberZoomState()
                SubcomposeAsyncImage(modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { clip = true }
                    .zoomable(zoomState),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(flightBrief.sigchart.uri).setHeader("User-Agent", "Team18")
                        .crossfade(500).build(),
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MetarTaf(metarTaf: MetarTaf?) {
    val clipboardManager = LocalClipboardManager.current
    val metar = metarTaf?.latestMetar
    val taf = metarTaf?.latestTaf
    val pageState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    TabRow(selectedTabIndex = pageState.currentPage) {
        Tab(selected = pageState.currentPage == 0,
            onClick = { scope.launch { pageState.animateScrollToPage(0) } },
            text = { Text("Raw") })
        Tab(selected = pageState.currentPage == 1,
            onClick = { scope.launch { pageState.animateScrollToPage(1) } },
            text = { Text("Decode") })

    }
    HorizontalPager(state = pageState) { index ->
        when (index) {
            0 -> {
                Column {
                    Text(text = "METAR:", fontWeight = FontWeight.Bold)
                    if (metar != null) {
                        Text(text = metar.text, modifier = Modifier.clickable {
                            clipboardManager.setText(
                                AnnotatedString(metar.text)
                            )
                        })
                    } else {
                        Text("No METAR available")
                    }
                    if (taf != null) {
                        Text(text = "TAF:", fontWeight = FontWeight.Bold)
                        Text(text = taf.text, modifier = Modifier.clickable {
                            clipboardManager.setText(
                                AnnotatedString(taf.text)
                            )
                        })
                    } else {
                        Text("No Taf available")
                    }
                }
            }

            1 -> {
                if (metar != null) {
                    Card {
                        MetarDecode(metar = metar.text)
                    }

                } else {
                    Text("No METAR available")
                }
            }
        }

    }
}

@Composable
fun MetarDecode(metar: String) {
    val parser = MetarParser.current()
    val decode = parser.parse(metar)
    Column {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .border(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {

                Text(text = "Metar", fontWeight = FontWeight.Bold)
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Station name: ")
                    }
                    append(decode.station.toString())
                })
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Report time: ")
                    }
                    val localTime = LocalDateTime.parse(decode.reportTime.toString())
                    val zonedDateTimeGMT = ZonedDateTime.of(localTime, ZoneId.of("GMT"))
                    val zoneIdNorway = ZoneId.of("Europe/Oslo")
                    val zonedDateTimeNorway = zonedDateTimeGMT.withZoneSameInstant(zoneIdNorway)
                    val formattedTime = zonedDateTimeNorway.format(DateTimeFormatter.ofPattern("HH:mm"))
                    append("${decode.reportTime?.date} at $formattedTime local time.")
                })
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Cavok status: ")
                    }
                    append(decode.ceilingAndVisibilityOK.toString())
                })
                Spacer(modifier = Modifier.height(10.dp))
                if (decode.wind != null) {
                    val wind = decode.wind
                    Text(text = "Wind", fontWeight = FontWeight.Bold)
                    if (wind != null) {
                        when (wind.isCalm) {
                            true -> Text(text = "All clear and no winds")
                            false -> when (wind.variable) {
                                true -> Text(text = "Wind is variable at ${wind.speed} KT")
                                false -> Text(buildAnnotatedString {
                                    append("Direction: ${wind.direction}°.\n")
                                    append("Speed: ${wind.speed} ${decode.wind?.speedUnits}")
                                })
                            }
                        }
                    }
                    if (wind != null) {
                        if (wind.gusts != 0) {
                            Text(text = "Gusts: ${wind.gustsKt} ${wind.speedUnits}")
                        }
                    }
                }
                val raw = decode.raw.split(" ")
                if (raw[3].length > 4 && raw[3].contains("V")) {
                    Text(text = "Variable winds from ${raw[3].take(3)}° to ${raw[3].takeLast(3)}°")
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Visibility", fontWeight = FontWeight.Bold)
                if (decode.visibility != null) {
                    val vis = decode.visibility
                    when (vis!!.distAll) {
                        9999 -> Text(text = "Visibility: >10 km")
                        else -> {
                            val distToKm = vis.distAll!!.div(1000.0)
                            val result = floor(distToKm * 10) / 10
                            Text(text = "Visibility: $result km")
                        }
                    }
                    if (vis.byDirections.isNotEmpty()) {
                        Text(text = "Remark", fontWeight = FontWeight.Bold)
                        Text(text = "Directions: ${vis.byDirections.joinToString(", ")}")
                    }
                    if (vis.byRunways.isNotEmpty()) {
                        Text(text = "Lowest visual: ${vis.byRunways.joinToString(", ")} ${vis.distUnits}")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Weather conditions:", fontWeight = FontWeight.Bold)

                if(decode.phenomenons.isNotEmpty()){
                    var qualifier = decode.phenomenons[0].intensity.toString()
                        .substring(0, 1) +
                            decode.phenomenons[0].intensity.toString()
                                .substring(1)
                                .lowercase(Locale.ROOT)
                    if (qualifier == "None") {
                        qualifier = "Moderate"
                    }
                    var phenomenon =
                        decode.phenomenons[0].group.joinToString(separator = " ").substring(0, 1) +
                                decode.phenomenons[0].group.joinToString(separator = " ")
                                    .substring(1).lowercase(Locale.ROOT)
                    if (phenomenon.contains("In_vicinity")) {
                        phenomenon = phenomenon.substring(12)
                        Text(text = "$qualifier $phenomenon in the vicinity")
                    } else {
                        Text(text = "$qualifier $phenomenon")
                    }

                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Clouds: ", fontWeight = FontWeight.Bold)
                val clouds = decode.clouds[0].type.toString()
                val cloudString =
                    clouds.substring(0, 1) + clouds.substring(1).lowercase(Locale.ROOT)
                when (decode.clouds[0].type.toString()) {
                    "CLEAR" -> Text(text = "Sky is clear")
                    "NIL_SIGNIFICANT" -> Text(text = "No significant clouds. Layer at ${decode.clouds[0].lowMarginFt * 100} ft")
                    else -> {
                        Text(text = "$cloudString layer at ${decode.clouds[0].lowMarginFt * 100} ft")
                    }
                }
                val cumulus = decode.clouds[0].cumulusType
                if (cumulus != null) {
                    Text(text = "Cumulus Type: " + decode.clouds[0].cumulusType)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Temperature", fontWeight = FontWeight.Bold)
                Text(text = "Air temperature: ${decode.temperature?.air} °C")
                Text(text = "Dew point: ${decode.temperature?.dewPoint} °C")

                Spacer(modifier = Modifier.height(10.dp))
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text = "Pressure: ")
                    }
                    append("${decode.pressureQNH?.hPa.toString()} hPa")
                })
            }
        }
    }
}


@Composable
fun Collapsible(
    header: String, expanded: Boolean = false, content: @Composable BoxScope.() -> Unit
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
                modifier = Modifier.padding(16.dp),
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

@Composable
fun WeatherDayCard(weatherDay: WeatherDay) {
    var expand by rememberSaveable {
        mutableStateOf(false)
    }

    val originalHourList = weatherDay.weather
    val groupedHourList = weatherDay.weather.chunked(6)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = weatherDay.date,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End)
        )
        groupedHourList.forEach { hour ->
            val firstHourInterval = hour.first()
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${firstHourInterval.hour}-${hour.last().hour}")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "${firstHourInterval.weatherDetails.air_temperature}")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "${firstHourInterval.weatherDetails.air_pressure_at_sea_level}")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "${firstHourInterval.weatherDetails.wind_speed}")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "${firstHourInterval.weatherDetails.relative_humidity}")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "${firstHourInterval.weatherDetails.cloud_area_fraction}")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "${firstHourInterval.weatherDetails.wind_from_direction}")
            }
            HorizontalDivider(
                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun WeatherHourScreen(weatherHour: WeatherHour) {
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LightPreviewFlightBrief() {
    FlightBreifScreenContent(
        flightBrief = FlightBrief(
            departure = AirportBrief(
                airport = Airport(
                    icao = Icao("ENGM"), name = "Gardermoen", Position(0.0, 0.0)
                ),
                metarTaf = MetarTaf(listOf(Metar("")), listOf()),
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

@Preview(showSystemUi = true)
@Composable
fun TestMetarDecode() {
    val test = "ENGM 080850Z 18003G10KT 150V240 9988 -VCSNTS NSC009 M08/M06 Q1005 TEMPO BKN012="
    MetarDecode(metar = test)
}

@Composable
fun DisplayTurbulence(airportBrief: AirportBrief) {

    var selectedTime by rememberSaveable { mutableStateOf(airportBrief.turbulence?.currentTurbulenceTime()) }
    var selectedDay by rememberSaveable { mutableStateOf(ZonedDateTime.now(ZoneOffset.UTC).dayOfWeek.name) }

    val turbulence = airportBrief.turbulence
    val mapDict = turbulence?.mapDict
    val crossDict = turbulence?.crossSectionDict

    val timeMap = turbulence?.allTurbulenceTimes()
    val times = timeMap?.get(selectedDay)


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (times != null && selectedTime != null) {
            MultiToggleButton(currentSelection = selectedDay,
                toggleStates = timeMap.keys.toList(),
                { onToggleChange -> selectedDay = onToggleChange })

            TurbulenceTimecards(selectedTime!!, times) { onCardClicked ->
                selectedTime = onCardClicked
            }

            mapDict?.get(selectedTime)?.let { DisplayTurbulenceImage(uri = it) } ?: run {
                Text("Image not available for time:\n $selectedTime")
            }

            HorizontalDivider(
                modifier = Modifier.padding(all = 5.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground
            )

            crossDict?.get(selectedTime)?.let { DisplayTurbulenceImage(uri = it) } ?: run {
                Text("Image not available for time:\n $selectedTime")
            }

        } else {
            Text(text = "Turbulence not available for ${airportBrief.airport.name}")
        }
    }
}


@Composable
fun TurbulenceTimecards(
    currentTime: ZonedDateTime, times: List<ZonedDateTime>, onCardClicked: (ZonedDateTime) -> Unit
) {

    val selectedTint = MaterialTheme.colorScheme.surfaceTint
    val unselectedTint = Color.Unspecified

    LazyRow(
        horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(all = 10.dp)
    ) {

        itemsIndexed(times) { _, time ->
            val isSelected = currentTime == time
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if (isSelected) Color.White else Color.Unspecified

            Card(colors = CardColors(
                containerColor = backgroundTint,
                contentColor = backgroundTint,
                disabledContainerColor = backgroundTint,
                disabledContentColor = backgroundTint
            ),
                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                onClick = { onCardClicked(time) }) {
                Text(
                    modifier = Modifier.padding(all = 5.dp),

                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = textColor//TODO: Display local time-format
                )
            }
        }
    }
}

@Composable
fun DisplayTurbulenceImage(uri: String) {
    SubcomposeAsyncImage(
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
        model = ImageRequest.Builder(LocalContext.current).data(uri)
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
}


@Composable
fun MultiToggleButton(
    currentSelection: String, toggleStates: List<String>, onToggleChange: (String) -> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.surfaceTint
    val unselectedTint = Color.Unspecified

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(shape = RoundedCornerShape(20.dp))


    ) {
        toggleStates.forEachIndexed { _, toggleState ->
            val isSelected = currentSelection.lowercase() == toggleState.lowercase()
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if (isSelected) Color.White else Color.Unspecified


            Row(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(backgroundTint)
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .toggleable(value = isSelected, enabled = true, onValueChange = { selected ->
                        if (selected) {
                            onToggleChange(toggleState)
                        }
                    })
            ) {
                Text(
                    toggleState.uppercase(), color = textColor, modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}


