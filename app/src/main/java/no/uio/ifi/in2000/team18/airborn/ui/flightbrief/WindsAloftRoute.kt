package no.uio.ifi.in2000.team18.airborn.ui.flightbrief


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
        padding = 0.dp
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
                    Text(text = "Departure")
                    Text(
                        text = "${routeIsobaric.departure.name.substringBefore(" ")} (${routeIsobaric.departure.icao})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(end = 0.dp)
                    )
                }
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Arrow",
                    modifier = Modifier
                        .height(30.dp)
                        .align(Alignment.Bottom)
                )
                Column {
                    Text(text = "Arrival")
                    Text(
                        text = "${routeIsobaric.arrival.name.substringBefore(" ")} " +
                                "(${routeIsobaric.departure.icao})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .height(IntrinsicSize.Max)
                            .padding(start = 0.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedCard(
                    Modifier.padding(horizontal = 8.dp),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Distance: ${routeIsobaric.distance.formatAsNm()}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                OutlinedCard(
                    Modifier.padding(horizontal = 8.dp),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Bearing: ${routeIsobaric.bearing}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                        RotatableArrowIcon(direction = Direction(routeIsobaric.bearing.degrees - 180.0))
                    }
                }
            }
            TableContent(isobaricData = routeIsobaric.isobaric)
            val validTo = routeIsobaric.isobaric.time.plusHours(3)
            Text(text = "Valid until: $validTo", Modifier.padding(all = 8.dp))
        }
    }
