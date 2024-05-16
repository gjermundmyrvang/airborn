package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.Turbulence
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.systemHourMinute
import no.uio.ifi.in2000.team18.airborn.ui.common.systemMonthDayHourMinute

@Composable
fun Turbulence(state: LoadingState<Map<String, List<Turbulence>>?>, initTurbulence: () -> Unit) =
    LazyCollapsible(
        header = "Turbulence", value = state, onExpand = initTurbulence,
    ) { turbulence ->
        if (turbulence == null) {
            Text("Selected airport does not have turbulence data", Modifier.padding(start = 10.dp))
            return@LazyCollapsible
        }
        var currentlySelectedType by rememberSaveable { mutableStateOf(turbulence.keys.first()) }
        var selectedTime by rememberSaveable { mutableIntStateOf(0) }
        val selectedMap = turbulence[currentlySelectedType]
        var date by rememberSaveable { mutableStateOf("") }
        MultiToggleButton(currentSelection = currentlySelectedType,
            toggleStates = turbulence.keys.toList(),
            onToggleChange = {
                currentlySelectedType = it; selectedTime = 0
            })
        if (selectedMap != null) {
            date = selectedMap[selectedTime].params.time.systemMonthDayHourMinute()
            TimeRow(modifier = Modifier.padding(start = 10.dp),
                current = selectedTime,
                times = selectedMap.map { it.params.time.systemHourMinute() },
                selectedColor = MaterialTheme.colorScheme.secondary,
                notSelectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                onTimeClicked = { selectedTime = it })
        }
        selectedMap?.get(selectedTime)?.let {
            ImageComposable(
                uri = it.uri,
                contentDescription = "Image of Turbulence $currentlySelectedType at $date",
                modifier = Modifier.aspectRatio(700f / 622f)
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                date,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }