package no.uio.ifi.in2000.team18.airborn.ui.flightbrief


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.RouteIsobaric
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.nauticalMiles
import no.uio.ifi.in2000.team18.airborn.model.round
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon
import no.uio.ifi.in2000.team18.airborn.ui.common.hourMinute
import no.uio.ifi.in2000.team18.airborn.ui.common.toSystemZoneOffset
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Composable
fun WindsAloftRoute(
    state: LoadingState<RouteIsobaric>,
    initRouteIsobaric: () -> Unit,
    onUpdateIsobaric: (Distance, ZonedDateTime) -> Unit
) = LazyCollapsible(
    header = "Winds Aloft",
    value = state,
    onExpand = initRouteIsobaric,
) { routeIsobaric ->
    Column(
        Modifier.padding(vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 25.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column {
                Text(
                    text = "${routeIsobaric.departure.icao}",
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = routeIsobaric.departure.name.substringBefore(" "),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Arrow forward",
                modifier = Modifier
                    .height(30.dp)
                    .align(Alignment.Bottom)
            )
            Column {
                Text(
                    text = "${routeIsobaric.departure.icao}",
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "${routeIsobaric.arrival.name.substringBefore(" ")} ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.height(IntrinsicSize.Max)
                )
            }
        }
        val timeStrings =
            routeIsobaric.isobaric.timeSeries.map { it.toSystemZoneOffset().hourMinute() }
        DistanceToIsobaricSlider(
            totalDistance = routeIsobaric.distance,
            bearing = routeIsobaric.bearing,
            airports = Pair(routeIsobaric.departure, routeIsobaric.arrival),
            currentPos = routeIsobaric.currentPos,
            onFractionSelected = { distance, time ->
                onUpdateIsobaric(distance, routeIsobaric.isobaric.timeSeries[time])
            },
            timeStrings = timeStrings
        )
        TableContent(isobaricData = routeIsobaric.isobaric)
    }
}

@Composable
fun DistanceToIsobaricSlider(
    totalDistance: Distance,
    bearing: Direction,
    airports: Pair<Airport, Airport>,
    currentPos: Position,
    onFractionSelected: (Distance, Int) -> Unit,
    timeStrings: List<String>
) = Column(
    Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .background(Color(0x801D1D1D), RoundedCornerShape(8.dp))
        .clip(RoundedCornerShape(8.dp)),
) {
    val distanceNm = totalDistance.nauticalMiles.toFloat()
    var sliderPosition by rememberSaveable { mutableFloatStateOf(0f) }
    var buttonEnabled by rememberSaveable {
        mutableStateOf(false)
    }
    var time by rememberSaveable {
        mutableIntStateOf(0)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text("Data from position:")
            Text("Latitude: ${currentPos.latitude.round(2)}")
            Text("Longitude: ${currentPos.longitude.round(2)}")
        }
        Column(horizontalAlignment = Alignment.End) {
            RotatableArrowIcon(direction = bearing, flip = false)
            Row {
                Text("Bearing: ")
                Text(bearing.formatAsDegrees())
            }
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(airports.first.icao.code, color = MaterialTheme.colorScheme.secondary)
        Text(airports.second.icao.code, color = MaterialTheme.colorScheme.secondary)
    }
    Slider(
        modifier = Modifier.padding(horizontal = 5.dp),
        value = sliderPosition,
        onValueChange = { sliderPosition = it; buttonEnabled = true },
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.background,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        valueRange = 0f..distanceNm
    )
    Text(text = "Distance traveled: ${sliderPosition.roundToInt()} nm / ${distanceNm.roundToInt()} nm")
    Button(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp),
        onClick = {
            onFractionSelected(sliderPosition.nauticalMiles, time); buttonEnabled = false
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        enabled = buttonEnabled
    ) {
        Text("Update isobaric data")
    }
    TimeRow(
        current = time,
        times = timeStrings,
        selectedColor = MaterialTheme.colorScheme.secondary,
        notSelectedColor = MaterialTheme.colorScheme.tertiaryContainer,
        onTimeClicked = { time = it; buttonEnabled = true },
        modifier = Modifier.align(Alignment.Start)
    )
}