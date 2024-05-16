package no.uio.ifi.in2000.team18.airborn.ui.home

import android.Manifest
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import no.uio.ifi.in2000.team18.airborn.R

@ExperimentalPermissionsApi
@Composable
fun LocationPermissionRequest(
) {
    val finePermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val coarsePermissionState = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
    var askForPermission by remember { mutableStateOf(true) }
    val context = LocalContext.current
    if (!askForPermission) return
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    if (sharedPreferences.getBoolean("first_time_opened", true)) {
        when {
            coarsePermissionState.status.isGranted && finePermissionState.status.shouldShowRationale -> {
                LocationPermissionDialog(message = "This app works better with precise location",
                    onDismissRequest = { askForPermission = false },
                    onClick = {
                        finePermissionState.launchPermissionRequest()
                    })
            }

            finePermissionState.status.shouldShowRationale -> {
                LocationPermissionDialog(message = "This app need access to your location to show where you are",
                    onDismissRequest = { askForPermission = false },
                    onClick = {
                        finePermissionState.launchPermissionRequest()
                    })
            }

            !finePermissionState.status.isGranted -> {
                LaunchedEffect(false) {
                    finePermissionState.launchPermissionRequest()
                }
            }
        }
        saveFirstTimeOpenedFlag(context)
    }
}

fun saveFirstTimeOpenedFlag(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("first_time_opened", false)
    editor.apply()
}

@Preview(showSystemUi = true)
@Composable
fun PreviewLocationPermissionDialog() {
    var enableLocation by remember { mutableStateOf(false) }
    LocationPermissionDialog(
        message = "This app uses your live location",
        onDismissRequest = {
            enableLocation = false
        },
    )
}

@Composable
fun LocationPermissionDialog(
    onClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    message: String,
) = Dialog(
    onDismissRequest = onDismissRequest
) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp, bottom = 20.dp)
            .width(300.dp)
            .height(370.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
            )
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.red_marker),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .height(120.dp)
                    .fillMaxWidth(),

                )
            Text(
                text = "Enable location",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    //  .padding(top = 5.dp)
                    .fillMaxWidth(),
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                message,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                    .fillMaxWidth(),
                letterSpacing = 1.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            val cornerRadius = 16.dp
            val gradientColors = listOf(Color(0xFFff669f), Color(0xFFff8961))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp),
                onClick = onClick,
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(cornerRadius)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(colors = gradientColors),
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Enable",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onDismissRequest) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
