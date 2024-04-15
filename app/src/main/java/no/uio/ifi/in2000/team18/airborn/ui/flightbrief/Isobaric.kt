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


@Composable
fun IsobaricData(state: LoadingState<IsobaricData?>) =
    LoadingCollapsible(state, header = "Isobaric data") { isobaric ->
        // data from isobaric layers, includes height TODO: a table or chart would be nice
        Text(text = "${isobaric?.time}")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("height", fontWeight = FontWeight.Bold)
            Text("temp", fontWeight = FontWeight.Bold)
            Text("speed", fontWeight = FontWeight.Bold)
            Text("dir", fontWeight = FontWeight.Bold)
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
                Text("%.0f m".format(it.height))
                Text("%.1f C".format(it.temperature - 273.15))
                Text("$it.windSpeed?.knots")
                Text("$it.windFromDirection?.degrees")
            }
        }
    }
