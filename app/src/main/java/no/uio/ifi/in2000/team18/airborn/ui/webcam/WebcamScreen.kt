package no.uio.ifi.in2000.team18.airborn.ui.webcam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.LoadingCollapsible

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebcamSection(state: LoadingState<List<Webcam>>) =
    LoadingCollapsible(state, header = "Webcams") { webcams ->
        var expanded by remember { mutableStateOf(false) }
        if (webcams.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No webcams available in 20km radius", fontSize = 30.sp
                )
            }
        } else {
            var currentWebcam by remember { mutableStateOf(webcams.first()) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
                    expanded = it
                }) {
                    OutlinedTextField(
                        value = currentWebcam.title,
                        onValueChange = {
                            expanded = true
                        },
                        readOnly = true,
                        label = { Text("Change webcam") },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = {
                        expanded = false
                    }) {
                        webcams.forEach { webcam ->
                            DropdownMenuItem(
                                {
                                    Text(webcam.title)
                                },
                                onClick = {
                                    currentWebcam = webcam
                                    expanded = false
                                },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentWebcam.images.current.preview).build(),
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
                    contentDescription = "Webcam image"
                )
            }
        }
    }