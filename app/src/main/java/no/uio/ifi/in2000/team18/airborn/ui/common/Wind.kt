package no.uio.ifi.in2000.team18.airborn.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.R

@Composable
fun RotatableArrowIcon(
    direction: Double,
    modifier: Modifier = Modifier,
    iconSize: Dp = 25.dp,
    iconColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val arrowIcon: Painter = painterResource(id = R.drawable.arrow_up)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = arrowIcon,
            contentDescription = "Arrow icon",
            modifier = modifier
                .size(iconSize)
                .rotate((direction - 180).toFloat()),
            colorFilter = ColorFilter.tint(iconColor)
        )
    }
}