package no.uio.ifi.in2000.team18.airborn.ui.flightbrief


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.RouteIsobaric
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon

@Composable
fun Route(state: LoadingState<RouteIsobaric>, initRouteIsobaric: () -> Unit) =
    LazyCollapsible(
        header = "Route isobaric",
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
                        modifier = Modifier
                            .height(IntrinsicSize.Max)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                OutlinedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxHeight(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .fillMaxHeight(),
                    ) {
                        Text(
                            text = "Distance: ${routeIsobaric.distance.formatAsNm()}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                OutlinedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxHeight(),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Bearing from ${routeIsobaric.departure.name.substringBefore(" ")}: ${routeIsobaric.bearing}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                        RotatableArrowIcon(
                            direction = Direction(routeIsobaric.bearing.degrees - 180.0),
                            iconColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
            }
            TableContent(isobaricData = routeIsobaric.isobaric)
        }
@Composable
fun DistanceToIsobaricSlider(
    totalDistance: Distance, onFractionSelected: (Distance) -> Unit
) = Column(
    Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .background(Color(0x801D1D1D), RoundedCornerShape(8.dp))
        .clip(RoundedCornerShape(8.dp)), horizontalAlignment = Alignment.CenterHorizontally
) {
    val distanceNm = totalDistance.nauticalMiles.toFloat()
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Departure")
        Text("Arrival")
    }
    Slider(
        modifier = Modifier.padding(horizontal = 5.dp),
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
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
            onFractionSelected(sliderPosition.nauticalMiles)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        Text("Update isobaric data")
    }
}