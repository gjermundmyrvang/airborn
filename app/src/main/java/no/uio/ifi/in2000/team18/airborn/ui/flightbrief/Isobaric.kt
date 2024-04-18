package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon


@Composable
fun IsobaricData(state: LoadingState<IsobaricData?>) =
    LazyCollapsible(header = "Winds aloft", value = state) { isobaric ->

        // data from isobaric layers, includes height TODO: a table or chart would be nice
        Text(text = "${isobaric?.time}")
        Spacer(modifier = Modifier.height(16.dp))
        if (isobaric == null) {
            return@LazyCollapsible
        }
        TableContent(isorbaricData = isobaric)
    }

@Composable
private fun TableContent(isorbaricData: IsobaricData) {
    LazyColumn(
        Modifier
            .padding(8.dp)
            .heightIn(min = 0.dp, max = 800.dp)
    ) {
        val column1Weight = .3f
        val column2Weight = .2f
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
        items(isorbaricData.data) { data ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.height?.let {
                    TableCell(
                        text = it.toStringAsFeet(),
                        weight = column1Weight,
                        alignment = TextAlign.Left,
                    )
                }
                TableCell(text = data.temperature.toString(), weight = column2Weight)
                TableCell(text = data.windSpeed.toString(), weight = column3Weight)
                data.windFromDirection?.let {
                    IconCell(
                        text = data.windFromDirection.toString(),
                        weight = column3Weight,
                        alignment = TextAlign.Right,
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

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false,
) {
    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment,
    )
}

@Composable
fun RowScope.IconCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false,
    windDirection: Direction
) {
    Row(
        Modifier
            .weight(weight)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            Modifier
                .weight(weight)
                .padding(10.dp),
            fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
            textAlign = alignment,
        )
        RotatableArrowIcon(direction = windDirection)
    }
}