package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.model.RouteForecast
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState


@Composable
fun RouteForecast(state: LoadingState<List<RouteForecast>>, initRoute: () -> Unit) =
    LazyCollapsible(
        header = "Route forecast", value = state, onExpand = { initRoute() }, padding = 0.dp
    ) { routeForecasts ->
        val route = routeForecasts.first().params.route
        Text( // All elements in list will be same route
            text = route, Modifier.padding(start = 10.dp)
        )
        var selected by rememberSaveable { mutableIntStateOf(0) }
        TimeRow(
            modifier = Modifier.padding(start = 10.dp),
            current = selected,
            times = routeForecasts.map { it.params.time.time },
            selectedColor = MaterialTheme.colorScheme.secondary,
            notSelectedColor = MaterialTheme.colorScheme.tertiaryContainer,
            onTimeClicked = { selected = it }
        )
        ImageComposable(
            uri = routeForecasts[selected].uri,
            contentDescription = "Route forecast",
            modifier = Modifier.aspectRatio(700f / 622F)
        )
    }