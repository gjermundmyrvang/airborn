package no.uio.ifi.in2000.team18.airborn.ui.flightbrief


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.degrees
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon


@Composable
fun Route(state: LoadingState<IsobaricData>, initRouteIsobaric: () -> Unit) =
    LazyCollapsible(header = "Route", value = state, onExpand = initRouteIsobaric) { isobaricData ->

        val departure = "Gardemoen Lufthavn*"
        val arrival = "Hamar flyplass*"

        val bearing = 359.degrees
        val distance = Distance(68900.0)

        Column {
            Row(
                Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${departure}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(end = 10.dp)
                )
                if (arrival != null) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Arrow",
                        modifier = Modifier.height(
                            30.dp
                        )
                    )
                    Text(
                        text = " ${arrival}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .height(IntrinsicSize.Max)
                            .padding(start = 10.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
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
                            text = "Distance: ${distance.formatAsNm()}",
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
                            text = "Bearing: ${bearing}",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                        RotatableArrowIcon(direction = Direction(bearing.degrees - 180.0))
                    }
                }
            }
            RouteTable(isobaricData = isobaricData)
            val validTo = isobaricData.time.plusHours(3)
            Text(text = "Valid until: $validTo", Modifier.padding(all = 8.dp))
            //TODO: Format time
        }
    }


@Composable
private fun RouteTable(isobaricData: IsobaricData) {
    LazyColumn(
        Modifier
            .padding(8.dp)
            .heightIn(min = 0.dp, max = 800.dp)
    ) {
        val column1Weight = .25f
        val column2Weight = .25f
        val column3Weight = .25f
        val column4Weight = .25f
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableCell(
                    text = "Height",
                    weight = column1Weight,
                    alignment = TextAlign.Left,
                    title = true
                )
                TableCell(text = "Temp", weight = column2Weight, title = true)
                TableCell(text = "Speed", weight = column3Weight, title = true)
                TableCell(
                    text = "Direction",
                    weight = column4Weight,
                    alignment = TextAlign.Right,
                    title = true
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                color = Color.LightGray
            )
        }
        items(isobaricData.data) { data ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.height?.let {
                    TableCell(
                        text = it.formatAsFeet(),
                        weight = column1Weight,
                        alignment = TextAlign.Left,
                    )
                }
                TableCell(text = data.temperature.toString(), weight = column2Weight)
                data.windSpeed?.let { TableCell(it.formatAsKnots(), weight = column3Weight) }
                data.windFromDirection?.let {
                    IconCell(
                        text = data.windFromDirection.toString(),
                        weight = column4Weight,
                        arrangement = Arrangement.End,
                        windDirection = it
                    )
                }
            }
            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
        }
    }
}
