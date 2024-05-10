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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.RouteIsobaric
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.nauticalMiles
import no.uio.ifi.in2000.team18.airborn.model.round
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
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

@OptIn(ExperimentalMaterial3Api::class)
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
        .clip(RoundedCornerShape(8.dp)),
) {
    val distanceNm = totalDistance.nauticalMiles.toFloat()
    var sliderPosition by rememberSaveable { mutableFloatStateOf(distanceNm / 2) }
    var buttonEnabled by rememberSaveable {
        mutableStateOf(false)
    }
    var time by remember {
        mutableIntStateOf(0)
    }
    Row(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "${airports.first.icao}", color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = airports.first.name.substringBefore(" "),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }
        Column {
            Text(
                text = "${airports.second.icao}", color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "${airports.second.name.substringBefore(" ")} ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.height(IntrinsicSize.Max)
            )
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it; buttonEnabled = true },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.background,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            thumb = {
                Icon(
                    painter = painterResource(id = R.drawable.local_airport_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.rotate(90.0F)
                )
            },
            valueRange = 0f..distanceNm
        )
        Text(text = "${sliderPosition.roundToInt()} nm / ${distanceNm.roundToInt()} nm")
    }
    TimeRow(
        current = time,
        times = timeStrings,
        selectedColor = MaterialTheme.colorScheme.secondary,
        notSelectedColor = MaterialTheme.colorScheme.tertiaryContainer,
        onTimeClicked = { time = it; buttonEnabled = true },
        modifier = Modifier.align(Alignment.Start)
    )
    Button(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), onClick = {
            onFractionSelected(sliderPosition.nauticalMiles, time); buttonEnabled = false
        }, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer
        ), enabled = buttonEnabled
    ) {
        Text("Update")
    }
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Position:", fontWeight = FontWeight.Bold)
            Row {
                Text("Lat: ${currentPos.latitude.round(2)} ")
                Text("Lon: ${currentPos.longitude.round(2)}")
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .padding(7.dp)
        ) {
            Compass(rotation = bearing.degrees.toFloat(), size = 70.dp)
            Row() {
                Text("Bearing: ", fontWeight = FontWeight.Bold)
                Text(bearing.formatAsDegrees(), fontWeight = FontWeight.Bold)
            }
        }
    }
}