package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState


@Composable
fun IsobaricData(state: LoadingState<IsobaricData?>, initisobaric: () -> Unit) = LazyCollapsible(
    header = "Winds aloft", value = state, onExpand = initisobaric, padding = 0.dp
) { isobaric ->
    // data from isobaric layers, includes height TODO: a table or chart would be nice
    Spacer(modifier = Modifier.height(16.dp))
    if (isobaric == null) {
        return@LazyCollapsible
    }
    TableContent(isobaricData = isobaric)
}

