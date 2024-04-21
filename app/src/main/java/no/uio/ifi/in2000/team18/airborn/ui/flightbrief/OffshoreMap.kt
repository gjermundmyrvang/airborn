package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.model.Labels
import no.uio.ifi.in2000.team18.airborn.model.OffshoreMap
import no.uio.ifi.in2000.team18.airborn.model.OffshoreParams
import no.uio.ifi.in2000.team18.airborn.ui.common.DateTime
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Composable
fun OffshoreMaps(state: LoadingState<Map<String, List<OffshoreMap>>>, initOffshoreMap: () -> Unit) =
    LazyCollapsible(
        header = "Offshore maps", value = state, onExpand = initOffshoreMap
    ) { offshoreMap ->
        val mapTypeOptions = listOf(
            "helicopterlightningobservations",
            "helicoptersignificantwaveheight",
            "helicoptertriggeredlightningindex",
        )
        val areaOptions = listOf(
            "norway", "northern_norway", "central_norway", "western_norway"
        )
        var selectedMap by rememberSaveable { mutableStateOf(mapTypeOptions[0]) }
        var selectedArea by rememberSaveable { mutableStateOf(areaOptions[0]) }
        var selectedOffshoreMapTime by rememberSaveable { mutableIntStateOf(0) }
        val offshoreMapList = offshoreMap[selectedMap]?.filter { it.params.area == selectedArea }
        OptionList(options = mapTypeOptions, currentlySelected = selectedMap) {
            selectedMap = it
        }
        OptionList(options = areaOptions, currentlySelected = selectedArea, indent = true) {
            selectedArea = it
        }
        if (offshoreMapList == null) {
            Text("Unable to find images that matches selected parameters")
            return@LazyCollapsible
        }
        TimeRow(
            offshoreList = offshoreMapList,
            currentlySelected = selectedOffshoreMapTime,
            onTimeClicked = { selectedOffshoreMapTime = it })
        ImageComposable(
            uri = offshoreMapList[selectedOffshoreMapTime].uri,
            contentDescription = "Offshore picture"
        )
    }

@Composable
fun OptionList(
    options: List<String>,
    currentlySelected: String,
    indent: Boolean = false,
    onOptionClicked: (String) -> Unit
) {
    options.forEach { option ->
        val isSelected = option == currentlySelected
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOptionClicked(option) },
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (indent) Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = if (isSelected) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                contentDescription = ""
            )
            Text(
                text = option.uppercase().replace("_", " "),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TimeRow(offshoreList: List<OffshoreMap>, currentlySelected: Int, onTimeClicked: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(offshoreList) { i, map ->
            val selected = i == currentlySelected
            Row(
                modifier = Modifier.clickable { onTimeClicked(i) },
                horizontalArrangement = Arrangement.Absolute.Left,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (selected) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                    contentDescription = ""
                )
                Text(text = "${map.params.time.dayNumberMonthTime} LT")
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showSystemUi = true)
@Composable
fun TestOffshoreMaps() {
    val exampleOffshoreMap = OffshoreMap(
        endpoint = "helicopterlightningobservations",
        params = OffshoreParams("norway", DateTime("2019-01-15T12:00:00Z")),
        labels = Labels("Norway"),
        updated = DateTime("2019-01-15T12:00:00Z"),
        uri = "https://api.met.no/weatherapi/offshoremaps/1.0/helicopterlightningobservations?area=northern_norway"
    )
    val exampleOffshoreMap2 = OffshoreMap(
        endpoint = "helicoptersignificantwaveheight",
        params = OffshoreParams("northern_norway", DateTime("2019-01-15T12:00:00Z")),
        labels = Labels("Norway"),
        updated = DateTime("2019-01-15T12:00:00Z"),
        uri = "https://api.met.no/weatherapi/offshoremaps/1.0/helicopterlightningobservations?area=northern_norway"
    )
    val exampleOffshoreMap3 = OffshoreMap(
        endpoint = "helicoptertriggeredlightningindex",
        params = OffshoreParams("norway", DateTime("2019-01-15T12:00:00Z")),
        labels = Labels("Norway"),
        updated = DateTime("2019-01-15T12:00:00Z"),
        uri = "https://api.met.no/weatherapi/offshoremaps/1.0/helicopterlightningobservations?area=northern_norway"
    )
    val testMap = mapOf("helicopterlightningobservations" to List(10) { exampleOffshoreMap },
        "helicoptersignificantwaveheight" to List(10) { exampleOffshoreMap2 },
        "helicoptertriggeredlightningindex" to List(10) { exampleOffshoreMap3 })
    val mapTypeOptions = listOf(
        "helicopterlightningobservations",
        "helicoptersignificantwaveheight",
        "helicoptertriggeredlightningindex",
    )
    val areaOptions = listOf(
        "norway", "northern_norway", "central_norway", "western_norway"
    )
    var selectedMap by rememberSaveable { mutableStateOf(mapTypeOptions[0]) }
    var selectedArea by rememberSaveable { mutableStateOf(areaOptions[0]) }
    var selectedOffshoreMapTime by rememberSaveable { mutableIntStateOf(0) }
    val offshoreMapList = testMap[selectedMap]?.filter { it.params.area == selectedArea }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        mapTypeOptions.forEach { mapType ->
            val isSelected = mapType == selectedMap
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMap = mapType },
                horizontalArrangement = Arrangement.Absolute.Left,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                    contentDescription = ""
                )
                Text(
                    text = mapType.uppercase(),
                    fontWeight = if (mapType == selectedMap) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        areaOptions.forEach { areaType ->
            val areaIsSelected = areaType == selectedArea
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.Left,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (areaIsSelected) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                    contentDescription = ""
                )
                Text(text = areaType.uppercase().replace("_", " "),
                    fontWeight = if (areaIsSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedArea = areaType })
            }
        }
        if (offshoreMapList == null) return@Column
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(offshoreMapList) { i, map ->
                val selected = i == selectedOffshoreMapTime
                Row(
                    modifier = Modifier.clickable { selectedOffshoreMapTime = i },
                    horizontalArrangement = Arrangement.Absolute.Left,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (selected) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                        contentDescription = ""
                    )
                    Text(text = "${map.updated.dayMonthHour} LT")
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}