package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.model.Radar
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Composable
fun RadarAnimations(state: LoadingState<List<Radar>>, initRadar: () -> Unit) = LazyCollapsible(
    header = "Radar", value = state, onExpand = { initRadar() },
) { radarList ->
    val options = listOf(
        "central_norway" to "Central Norway",
        "eastern_norway" to "Eastern Norway",
        "finnmark" to "Finnmark",
        "nordic" to "Nordic",
        "nordland" to "Nordland",
        "northern_nordland" to "Northern Nordland",
        "northwestern_norway" to "Northwestern Norway",
        "norway" to "Norway",
        "southeastern_norway" to "Southeastern Norway",
        "southern_nordland" to "Southern Nordland",
        "southern_norway" to "Southern Norway",
        "southwestern_norway" to "Southwestern Norway",
        "troms" to "Troms",
        "western_norway" to "Western Norway",
        "xband" to "X-band"
    )
    val ratioMap = mapOf(
        "central_norway" to Pair(700f, 640f),
        "eastern_norway" to Pair(579f, 677f),
        "finnmark" to Pair(820f, 653f),
        "nordic" to Pair(659f, 761f),
        "nordland" to Pair(580f, 756f),
        "northern_nordland" to Pair(737f, 680f),
        "northwestern_norway" to Pair(834f, 658f),
        "norway" to Pair(726f, 1037f),
        "southeastern_norway" to Pair(771f, 631f),
        "southern_nordland" to Pair(663f, 661f),
        "southern_norway" to Pair(890f, 919f),
        "southwestern_norway" to Pair(696f, 664f),
        "troms" to Pair(870f, 642f),
        "western_norway" to Pair(579f, 758f),
        "xband" to Pair(893f, 809f),
    )
    val typeList = mapOf(
        "5level_reflectivity" to "Reflectivity (5 levels)",
        "accumulated_01h" to "Accumulated Precipitation (1h)",
        "accumulated_02h" to "Accumulated Precipitation (2h)",
        "accumulated_03h" to "Accumulated Precipitation (3h)",
        "accumulated_04h" to "Accumulated Precipitation (4h)",
        "accumulated_05h" to "Accumulated Precipitation (5h)",
        "accumulated_06h" to "Accumulated Precipitation (6h)",
        "accumulated_07h" to "Accumulated Precipitation (7h)",
        "accumulated_08h" to "Accumulated Precipitation (8h)",
        "accumulated_09h" to "Accumulated Precipitation (9h)",
        "accumulated_10h" to "Accumulated Precipitation (10h)",
        "accumulated_11h" to "Accumulated Precipitation (11h)",
        "accumulated_12h" to "Accumulated Precipitation (12h)",
        "accumulated_13h" to "Accumulated Precipitation (13h)",
        "accumulated_14h" to "Accumulated Precipitation (14h)",
        "accumulated_15h" to "Accumulated Precipitation (15h)",
        "accumulated_16h" to "Accumulated Precipitation (16h)",
        "accumulated_17h" to "Accumulated Precipitation (17h)",
        "accumulated_18h" to "Accumulated Precipitation (18h)",
        "accumulated_19h" to "Accumulated Precipitation (19h)",
        "accumulated_20h" to "Accumulated Precipitation (20h)",
        "accumulated_21h" to "Accumulated Precipitation (21h)",
        "accumulated_22h" to "Accumulated Precipitation (22h)",
        "accumulated_23h" to "Accumulated Precipitation (23h)",
        "accumulated_24h" to "Accumulated Precipitation (24h)",
        "fir_preciptype" to "FIR Precipitation Type",
        "lx_reflectivity" to "Reflectivity (LX)",
        "preciptype" to "Precipitation Type",
        "reflectivity" to "Reflectivity"
    )

    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedTypeIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedArea by rememberSaveable { mutableStateOf(options[0].first) }
    val selectedList = radarList.filter { it.params.area == selectedArea }
    val types = selectedList.map { it.params.type }

    options.firstOrNull { it.first == selectedArea }?.second?.let {
        Text(
            text = it,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
    val widtRatio = ratioMap[selectedArea]?.first ?: 700f
    val heightRatio = ratioMap[selectedArea]?.second ?: 700f
    Spacer(modifier = Modifier.height(8.dp))
    GifComposable(
        uri = selectedList[selectedTypeIndex].uri,
        contentDescription = "",
        modifier = Modifier.aspectRatio(widtRatio / heightRatio)
    )
    Button(
        onClick = {
            dropdownExpanded = !dropdownExpanded
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text("Change area")
    }
    Column(
        Modifier
            .heightIn(250.dp, 500.dp)
            .fillMaxWidth(),
    ) {
        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false },
            Modifier
                .height(300.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            options.forEach { area ->
                DropdownMenuItem(text = {
                    Text(
                        area.second, color = MaterialTheme.colorScheme.primary
                    )
                }, onClick = {
                    selectedArea = area.first
                    selectedTypeIndex = 0
                    dropdownExpanded = false
                }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.background
                    )
                })
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth(), content = {
            itemsIndexed(types) { i, type ->
                val isSelected = i == selectedTypeIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTypeIndex = i
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.PlayArrow else Icons.Outlined.PlayArrow,
                        contentDescription = "Select icon",
                        tint = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiaryContainer
                    )
                    typeList[type]?.let {
                        Text(
                            text = it,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        })
    }
}