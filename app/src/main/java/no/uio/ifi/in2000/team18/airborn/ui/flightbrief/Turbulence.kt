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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.TurbulenceMapAndCross
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Turbulence(state: LoadingState<TurbulenceMapAndCross?>) =
    LoadingCollapsible(state, header = "Turbulence") { turbulence ->
        var selectedTime by rememberSaveable { mutableStateOf(turbulence?.currentTurbulenceTime()) }
        var selectedDay by rememberSaveable { mutableStateOf(ZonedDateTime.now(ZoneOffset.UTC).dayOfWeek.name) }

        val mapDict = turbulence?.mapDict
        val crossDict = turbulence?.crossSectionDict

        val timeMap = turbulence?.allTurbulenceTimes()
        val times = timeMap?.get(selectedDay)

        if (times == null || selectedTime == null) {
            Text(text = "Turbulence not available")
            return@LoadingCollapsible
        }

        MultiToggleButton(currentSelection = selectedDay, toggleStates = timeMap.keys.toList()) {
            selectedDay = it
        }

        TurbulenceTimecardRow(selectedTime!!, times) { onCardClicked ->
            selectedTime = onCardClicked
        }

        mapDict?.get(selectedTime)?.let { TurbulenceImage(uri = it) } ?: run {
            Text("Image not available for time:\n $selectedTime")
        }

        HorizontalDivider(
            modifier = Modifier.padding(all = 5.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )

        crossDict?.get(selectedTime)?.let { TurbulenceImage(uri = it) } ?: run {
            Text("Image not available for time:\n $selectedTime")
        }
    }


@Composable
fun TurbulenceTimecardRow(
    currentTime: ZonedDateTime, times: List<ZonedDateTime>, onCardClicked: (ZonedDateTime) -> Unit
) {

    val selectedTint = MaterialTheme.colorScheme.surfaceTint
    val unselectedTint = Color.Unspecified

    LazyRow(
        horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(all = 10.dp)
    ) {

        itemsIndexed(times) { _, time ->
            val isSelected = currentTime == time
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if (isSelected) Color.White else Color.Unspecified

            Card(colors = CardColors(
                containerColor = backgroundTint,
                contentColor = backgroundTint,
                disabledContainerColor = backgroundTint,
                disabledContentColor = backgroundTint
            ),
                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                onClick = { onCardClicked(time) }) {
                Text(
                    modifier = Modifier.padding(all = 5.dp),

                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = textColor//TODO: Display local time-format
                )
            }
        }
    }
}

@Composable
fun TurbulenceImage(uri: String) {
    val zoomState = rememberZoomState()
    SubcomposeAsyncImage(modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { clip = true }
        .zoomable(zoomState),
        contentScale = ContentScale.FillWidth,
        model = ImageRequest.Builder(LocalContext.current).data(uri)
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
        contentDescription = "Image of turbulence map"
    )
}

