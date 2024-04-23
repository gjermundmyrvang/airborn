package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.Area
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Sigchart(state: LoadingState<Map<Area, List<Sigchart>>>, initSigchart: () -> Unit) =
    LazyCollapsible(header = "Sigcharts", value = state, onExpand = initSigchart) { sigcharts ->
        var selectedArea by rememberSaveable { mutableStateOf(Area.norway) }
        var selectedSigchart by rememberSaveable { mutableIntStateOf(0) }

        val sigchartList = sigcharts[selectedArea]?.takeLast(3) ?: return@LazyCollapsible

        MultiToggleButton(currentSelection = selectedArea.toString(),
            toggleStates = listOf("norway", "nordic"),
            onToggleChange = { selectedArea = Area.valueOf(it) })

        SigchartTimecardRow(currentSigchart = selectedSigchart,
            sigcharts = sigchartList,
            onCardClicked = { selectedSigchart = it })

        ImageComposable(uri = sigchartList[selectedSigchart].uri, "Image of sigchart")
    }


@Composable
fun SigchartTimecardRow(
    currentSigchart: Int, sigcharts: List<Sigchart>, onCardClicked: (Int) -> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.surfaceTint
    val unselectedTint = Color.Unspecified
    Text(text = "Local time:", color = MaterialTheme.colorScheme.secondary, fontSize = 15.sp)
    LazyRow(
        horizontalArrangement = Arrangement.Center,
    ) {

        itemsIndexed(sigcharts) { i, sigchart ->
            val isSelected = currentSigchart == i
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = MaterialTheme.colorScheme.secondary
            val time = ZonedDateTime.parse(sigchart.params.time).withZoneSameInstant(
                ZoneId.systemDefault()
            )
            Column(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .clickable { onCardClicked(i) }
                    .width(intrinsicSize = IntrinsicSize.Min)
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(backgroundTint, RoundedCornerShape(10.dp))
                )
            }
        }
    }
}

