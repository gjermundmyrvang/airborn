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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.TurbulenceMapAndCross
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun Turbulence(state: LoadingState<TurbulenceMapAndCross?>, initTurbulence: () -> Unit) =
    LazyCollapsible(
        header = "Turbulence", value = state, onExpand = initTurbulence, padding = 0.dp
    ) { turbulence ->
        if (turbulence == null) {
            Error(
                message = "Selected airport does not have turbulence data",
                modifier = Modifier.padding(5.dp)
            )
            return@LazyCollapsible
        }
        var selectedTime by rememberSaveable { mutableStateOf(turbulence.currentTurbulenceTime()) }
        var selectedDay by rememberSaveable { mutableStateOf(ZonedDateTime.now(ZoneOffset.UTC).dayOfWeek.name) }

        val mapDict = turbulence.mapDict
        val crossDict = turbulence.crossSectionDict

        val timeMap = turbulence.allTurbulenceTimes()
        val times = timeMap?.get(selectedDay)


        if (timeMap != null) {
            MultiToggleButton(
                currentSelection = selectedDay, toggleStates = timeMap.keys.toList()
            ) {
                selectedDay = it
            }
        }

        if (times != null) {
            TurbulenceTimecardRow(selectedTime, times) { onCardClicked ->
                selectedTime = onCardClicked
            }
        }

        mapDict?.get(selectedTime)?.let { ImageComposable(uri = it, "Image of turbulence map") }
            ?: run {
                Text("Image not available for time:\n $selectedTime")
            }

        HorizontalDivider(
            modifier = Modifier.padding(all = 5.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )

        crossDict?.get(selectedTime)
            ?.let { ImageComposable(uri = it, "Image of turbulence cross-section") } ?: run {
            Text("Image not available for time:\n $selectedTime")
        }
    }


@Composable
fun TurbulenceTimecardRow(
    currentTime: ZonedDateTime, times: List<ZonedDateTime>, onCardClicked: (ZonedDateTime) -> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.secondary
    val unselectedTint = MaterialTheme.colorScheme.secondaryContainer

    Text(text = "Local time:", fontSize = 15.sp, modifier = Modifier.padding(start = 10.dp))
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
    ) {
        itemsIndexed(times) { _, time ->
            val isSelected = currentTime == time
            val backgroundTint = if (isSelected) selectedTint else unselectedTint

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(intrinsicSize = IntrinsicSize.Min)
                    .padding(end = 10.dp)
                    .clickable { onCardClicked(time) }) {
                Text(
                    text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    fontWeight = FontWeight.Medium,
                )
                Box(
                    Modifier
                        .background(
                            backgroundTint, RoundedCornerShape(5.dp)
                        )
                        .fillMaxWidth()
                        .height(3.dp)
                )
            }
        }
    }
}

