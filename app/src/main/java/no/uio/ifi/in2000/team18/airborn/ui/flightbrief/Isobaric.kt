package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon


@Composable
fun IsobaricData(state: LoadingState<IsobaricData?>) =
    LoadingCollapsible(state, header = "Winds Aloft") { isobaric ->
        // data from isobaric layers, includes height TODO: a table or chart would be nice
        Text(text = "${isobaric?.time}")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Height", fontWeight = FontWeight.Bold)
            Text("Temp", fontWeight = FontWeight.Bold)
            Text("Speed", fontWeight = FontWeight.Bold)
            Text("Direction", fontWeight = FontWeight.Bold)
            Text("", fontWeight = FontWeight.Bold)
        }
        isobaric?.data?.forEach {
            Log.d(
                "windsAloft",
                "height: ${it.height}, uWind: ${it.uWind}, vWind: ${it.vWind}"
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                it.height?.let { h -> Text(h.toStringAsFeet()) }
                Text(it.temperature.toString())
                Text(it.windSpeed.toString())
                Text(it.windFromDirection.toString())
                it.windFromDirection?.let { direction -> RotatableArrowIcon(direction) }
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