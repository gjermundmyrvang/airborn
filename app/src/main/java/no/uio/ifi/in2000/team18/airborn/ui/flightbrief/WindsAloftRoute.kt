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
import no.uio.ifi.in2000.team18.airborn.model.Route
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon


@Composable
fun Route(state: LoadingState<Route>, initRouteIsobaric: () -> Unit) =
    LazyCollapsible(
        header = "Route isobaric",
        value = state,
        onExpand = initRouteIsobaric,
        padding = 0.dp
    ) { route ->
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
                        text = "${route.departure.icao}",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = route.departure.name.substringBefore(" "),
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
                        text = "${route.departure.icao}",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${route.arrival.name.substringBefore(" ")} ",
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
                            text = "Distance: ${route.position?.distance?.formatAsNm()}",
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
                            text = "Bearing : ${route.position?.bearing}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                        val bearing = route.position?.bearing
                        if (bearing != null) {
                            RotatableArrowIcon(
                                direction = Direction(bearing.degrees - 180.0),
                                iconColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }
        }
        if (route.isobaric != null) {
            TableContent(isobaricData = route.isobaric!!)
        }
    }
