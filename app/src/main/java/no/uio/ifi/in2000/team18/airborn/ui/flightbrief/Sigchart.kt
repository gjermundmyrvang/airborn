package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Composable
fun Sigchart(state: LoadingState<Map<Area, List<Sigchart>>>, initSigchart: () -> Unit) =
    LazyCollapsible(
        header = "Sigcharts",
        value = state,
        onExpand = initSigchart,
    ) { sigcharts ->
        var selectedArea by rememberSaveable { mutableStateOf(Area.norway) }
        var selectedSigchart by rememberSaveable { mutableIntStateOf(0) }

        val sigchartList = sigcharts[selectedArea]?.takeLast(3) ?: return@LazyCollapsible

        MultiToggleButton(currentSelection = selectedArea.toString(),
            toggleStates = listOf("norway", "nordic"),
            onToggleChange = { selectedArea = Area.valueOf(it) })

        TimeRow(current = selectedSigchart,
            times = sigchartList.map { it.params.time.time },
            selectedColor = MaterialTheme.colorScheme.secondary,
            notSelectedColor = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(start = 10.dp),
            onTimeClicked = { selectedSigchart = it })

        ImageComposable(
            uri = sigchartList[selectedSigchart].uri,
            "Image of sigchart",
            modifier = Modifier.aspectRatio(595f / 841f),
        )
    }