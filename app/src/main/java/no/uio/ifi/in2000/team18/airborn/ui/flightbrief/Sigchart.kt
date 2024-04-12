package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Sigchart(state: LoadingState<Map<Area, List<Sigchart>>>) =
    LoadingCollapsible(state, header = "Sigchart") { sigcharts ->
        var selectedArea by rememberSaveable { mutableStateOf(Area.norway) }
        var selectedSigchart by rememberSaveable { mutableIntStateOf(0) }

        val sigchartList = sigcharts[selectedArea]?.takeLast(3)

        if (sigchartList == null) {
            return@LoadingCollapsible
        }

        MultiToggleButton(currentSelection = selectedArea.toString(),
            toggleStates = listOf("norway", "nordic"),
            onToggleChange = { selectedArea = Area.valueOf(it) })

        SigchartTimecardRow(currentSigchart = selectedSigchart,
            sigcharts = sigchartList,
            onCardClicked = { selectedSigchart = it })

        SigchartImage(sigchart = sigchartList[selectedSigchart])
    }

@Composable
fun SigchartImage(sigchart: Sigchart?) {
    val zoomState = rememberZoomState()
    SubcomposeAsyncImage(modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { clip = true }
        .zoomable(zoomState),
        contentScale = ContentScale.FillWidth,
        model = ImageRequest.Builder(LocalContext.current).data(sigchart?.uri)
            .setHeader("User-Agent", "Team18").crossfade(500).build(),
        loading = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 1.dp
                )
            }
        },
        contentDescription = "Image of sigchart. Updated at: $sigchart."
    )
}


@Composable
fun SigchartTimecardRow(
    currentSigchart: Int, sigcharts: List<Sigchart>, onCardClicked: (Int) -> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.surfaceTint
    val unselectedTint = Color.Unspecified

    LazyRow(
        horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(all = 10.dp)
    ) {

        itemsIndexed(sigcharts) { i, sigchart ->
            val isSelected = currentSigchart == i
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if (isSelected) Color.White else Color.Unspecified
            val time = ZonedDateTime.parse(sigchart.params.time).withZoneSameInstant(
                ZoneId.systemDefault()
            )

            Card(colors = CardColors(
                containerColor = backgroundTint,
                contentColor = backgroundTint,
                disabledContainerColor = backgroundTint,
                disabledContentColor = backgroundTint
            ),
                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                onClick = { onCardClicked(i) }) {
                Text(
                    modifier = Modifier.padding(all = 5.dp),
                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = textColor
                )
            }
        }
    }
}

