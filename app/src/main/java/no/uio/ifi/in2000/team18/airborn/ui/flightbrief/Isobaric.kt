package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

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


@Composable
fun IsobaricData(isobaric: IsobaricData?) = Collapsible(header = "Isobaric data") {
    // data from isobaric layers, includes height TODO: a table or chart would be nice
    Text(text = "${isobaric?.time}")
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("height", fontWeight = FontWeight.Bold)
        Text("temperature", fontWeight = FontWeight.Bold)
        Text("pressure", fontWeight = FontWeight.Bold)
    }
    isobaric?.data?.forEach {
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
