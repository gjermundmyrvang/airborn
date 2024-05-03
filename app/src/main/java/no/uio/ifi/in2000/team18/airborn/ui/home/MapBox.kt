package no.uio.ifi.in2000.team18.airborn.ui.home

import android.Manifest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseSigmet
import no.uio.ifi.in2000.team18.airborn.model.Distance
import no.uio.ifi.in2000.team18.airborn.model.Position
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import no.uio.ifi.in2000.team18.airborn.model.SigmetType
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.SunComposable


@OptIn(MapboxExperimental::class, ExperimentalPermissionsApi::class)
@Composable
fun Map(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    airportSelected: () -> Unit = {},
    sheetHeight: Dp,
) = Column(
    modifier = modifier,
) {

    val state by homeViewModel.state.collectAsState()
    val airports = state.airports
    val sigmets = state.sigmets
    var selectedAirport by remember { mutableStateOf<Airport?>(null) }
    var isClicked by remember { mutableStateOf(false) }
    var showAlertMessage by remember { mutableStateOf(false) }
    var sigmetClicked by rememberSaveable { mutableIntStateOf(0) }
    val finePermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION)
    val mapViewportState = rememberMapViewportState {
        if (permissionState.status.isGranted) {
            transitionToFollowPuckState(
                FollowPuckViewportStateOptions.Builder().zoom(4.000).pitch(0.0).build(),
                DefaultViewportTransitionOptions.Builder().build(),
            )
        } else {
            setCameraOptions {
                zoom(3.420)
                center(Point.fromLngLat(15.00, 69.69 - 8))
                pitch(0.0)
                bearing(0.0)
            }
        }
    }
    val showNoSigmetMessage = state.showNoSigmetMessage
    Box {
        val distance = state.airportPair?.let {
            it.first.position.distanceTo(it.second.position)
        }
        MapboxMap(
            mapViewportState = mapViewportState,
            locationComponentSettings = LocationComponentSettings(
                locationPuck = createDefault2DPuck(true)
            ) {
                enabled = true
                puckBearing = PuckBearing.HEADING
                puckBearingEnabled = true
            },
            style = { MapStyle(style = "mapbox://styles/gjerry10/clvntbvgq01ks01qv4p092dap") }
        ) {
            airports.forEach { airport ->
                Annotation(airport) {
                    selectedAirport = it
                }
            }
            if (sigmets.isNotEmpty()) {
                Polygons(sigmets = sigmets) {
                    isClicked = true
                    sigmetClicked = it
                }
            }
            state.airportPair?.let {
                Polyline(
                    positions = it
                )
            }
        }
        if (showAlertMessage) finePermissionState.launchPermissionRequest()
        Box(
            modifier = Modifier
                .offset(0.dp, -sheetHeight)
                .align(Alignment.BottomEnd),
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    if (!permissionState.status.isGranted) showAlertMessage = true
                    mapViewportState.transitionToFollowPuckState(
                        FollowPuckViewportStateOptions.Builder()
                            .zoom(7.000)
                            .pitch(0.0)
                            .build(),
                        DefaultViewportTransitionOptions.Builder().build(),
                    )
                }) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter =
                    painterResource(
                        id = R.drawable.center
                    ),
                    contentDescription = "Recenter"
                )
            }
        }
        Column(modifier = Modifier.padding(top = 6.dp)) {
            Spacer(modifier = Modifier.height(10.dp))
            if (showNoSigmetMessage) {
                NoSigmetInfoBox {
                    homeViewModel.dismissNoSigmetMessage()
                }
            }
            if (distance != null) {
                state.airportPair?.let { DistanceInfoBox(distance = distance, airportPair = it) }
            }
            selectedAirport?.let { airport ->
                homeViewModel.updateSunriseAirport(airport)
                InfoBox(
                    airport = airport,
                    state,
                    onClose = { selectedAirport = null },
                    addDeparture = {
                        selectedAirport = null
                        homeViewModel.selectDepartureAirport(it)
                        airportSelected()
                    },
                    addArrival = {
                        selectedAirport = null
                        homeViewModel.selectArrivalAirport(it)
                        airportSelected()
                    },
                )
            }
            if (isClicked) {
                SigmetInfoBox(sigmet = sigmets[sigmetClicked]) {
                    isClicked = false
                }
            }
        }
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun Polygons(
    sigmets: List<Sigmet>, onPolyClicked: (Int) -> Unit
) {
    sigmets.forEachIndexed { index, sigmet ->
        PolygonAnnotation(points = listOf(sigmet.coordinates.map { it.toPoints() }),
            fillOutlineColorInt = Color.Black.toArgb(),
            fillColorInt = if (sigmet.type == SigmetType.Airmet) Color.Cyan.copy(alpha = 0.4f)
                .toArgb() else Color.Yellow.copy(
                alpha = 0.4f
            ).toArgb(),
            fillOpacity = 1.0,
            onClick = {
                onPolyClicked(index)
                true
            })
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun Polyline(positions: Pair<Airport, Airport>) {
    val pos1 = positions.first.position
    val pos2 = positions.second.position
    PolylineAnnotation(
        points = listOf(pos1, pos2).map { it.toPoints() },
        lineColorInt = Color(0xFF1D1D1D).toArgb(),
        lineWidth = 3.0,
        onClick = {
            true
        },
    )
}


@OptIn(MapboxExperimental::class)
@Composable
fun Annotation(airport: Airport, onAirportClicked: (Airport) -> Unit) {
    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(airport.position.toPoints())
            allowOverlap(true)
            allowOverlapWithPuck(true)
            annotationAnchor {
                anchor(ViewAnnotationAnchor.CENTER)
            }
        },
    ) {
        Box(
            Modifier
                .size(26.dp)
                .background(Color.Transparent)
                .clickable { onAirportClicked(airport) }, contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.local_airport_24),
                contentDescription = "Marker",
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
fun NoSigmetInfoBox(onClose: () -> Unit) {
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(5.dp)
            )
            .clip(RoundedCornerShape(5.dp))
            .background(
                Color(0xD5263842),
                shape = RoundedCornerShape(5.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Text(
            "Currently no sigmets/airmets",
            Modifier.padding(start = 5.dp),
            MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { onClose() }) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close icon",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun DistanceInfoBox(distance: Distance, airportPair: Pair<Airport, Airport>) = Box(
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(5.dp)
        )
        .background(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            shape = RoundedCornerShape(5.dp)
        )
        .clip(
            RoundedCornerShape(5.dp)
        )
) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(), horizontalAlignment = Alignment.Start
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            val tint = MaterialTheme.colorScheme.secondary
            Text(
                airportPair.first.name.substringBefore(","),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
            Icon(painterResource(R.drawable.flight_takeoff), "takeoff", tint = tint)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                airportPair.second.name.substringBefore(","),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
            Icon(painterResource(R.drawable.flight_landing), "takeoff", tint = tint)
        }
        Text(
            "Distance between airports: ${distance.formatAsNm()}",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SigmetInfoBox(sigmet: Sigmet, onClose: () -> Unit) = Box(
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .background(
            color = if (sigmet.type == SigmetType.Airmet) Color.Cyan.copy(alpha = 0.8f) else Color.Yellow.copy(
                alpha = 0.8f
            ),
            shape = RoundedCornerShape(5.dp),
        )
        .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
        .clip(RoundedCornerShape(5.dp))
) {
    IconButton(onClick = { onClose() }, modifier = Modifier.align(Alignment.TopEnd)) {
        Icon(
            imageVector = Icons.Filled.Close, contentDescription = "Close icon", tint = Color.Black
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Row {
            Text("Type: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.type.toString(), color = Color.Black)
            Text(" (${sigmet.identifier.first}${sigmet.identifier.second})", color = Color.Black)
        }
        Row {
            Text("Weathermessage: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.message.joinToString(" ") { it }, color = Color.Black)
        }
        Row {
            Text("Location: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.location, color = Color.Black)
        }
        Row {
            Text("Origin location: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.originatingLocation, color = Color.Black)
        }
        Row {
            Text("Region code: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.regionCode, color = Color.Black)
        }
        Row {
            Text("Issuing authority: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.issuingAuthority, color = Color.Black)
        }
        val issued = sigmet.dateTime
        val validFrom = sigmet.timeRange.first
        val validTo = sigmet.timeRange.second
        Row {
            Text("Issued: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text("$issued", color = Color.Black)
        }
        Row {
            Text("Valid from: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text("$validFrom", color = Color.Black)
        }
        Row {
            Text("Valid to: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text("$validTo", color = Color.Black)
        }
        val base = "${sigmet.altitude?.first?.typ}/${sigmet.altitude?.first?.number}"
        val top = "${sigmet.altitude?.second?.number}"
        Row {
            Text("Base: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(base, color = Color.Black)
        }
        Row {
            Text("Top: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(top, color = Color.Black)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestSigmetInfoBox() {
    SigmetInfoBox(
        sigmet = parseSigmet(
            """
        ZCZC
        WSNO36 ENMI 211336
        ENOB SIGMET M01 VALID 211400/211800 ENMI-
        ENOB BODOE OCEANIC FIR SEV MTW FCST WI N7950 E01030 - N8000 E01730 - N7900 E02000 - N7900 E01100 - N7950 E01030 SFC/FL250 STNR INTSF=
    """.trimIndent()
        ).expect()
    ) {

    }
}

@Composable
fun InfoBox(
    airport: Airport,
    state: HomeViewModel.UiState,
    onClose: () -> Unit,
    addDeparture: (Airport) -> Unit,
    addArrival: (Airport) -> Unit
) = Box(
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(5.dp)
        )
        .background(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            shape = RoundedCornerShape(5.dp)
        )
        .clip(RoundedCornerShape(5.dp))
) {
    IconButton(
        onClick = { onClose() }, Modifier.align(Alignment.TopEnd)
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close icon",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        Text(
            "${airport.name} / ${airport.icao.code}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Lat: ${airport.position.latitude}",
            style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Lon: ${airport.position.longitude}",
            style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(15.dp))

        SunComposable(
            sun = state.sun,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            header = "Sun info:"
        )


        Row(
            Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = { addDeparture(airport) },
                modifier = Modifier
                    .height(35.dp)
                    .padding(end = 5.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Text(
                        text = " Departure",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(0.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            OutlinedButton(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = { addArrival(airport) },
                modifier = Modifier.height(height = 35.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Text(
                        text = " Arrival",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(0.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestInfoBox() {
    InfoBox(airport = Airport(
        icao = Icao("ENGM"), name = "Gardermoen", position = Position(59.11, 11.59)
    ), state = HomeViewModel.UiState(), {}, {}, {})
}


@Preview(showSystemUi = true)
@Composable
fun PreviewAnnotation() = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        painter = painterResource(id = R.drawable.red_marker),
        contentDescription = "Marker",
        modifier = Modifier.size(20.dp)
    )
}