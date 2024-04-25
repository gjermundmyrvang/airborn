package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import android.graphics.BlurMaskFilter
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import no.uio.ifi.in2000.team18.airborn.model.Direction
import no.uio.ifi.in2000.team18.airborn.model.isobaric.IsobaricData
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.common.RotatableArrowIcon
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTheme

@Composable
fun Error(
    message: String, modifier: Modifier = Modifier
) = Box(modifier = modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(width = 2.dp, color = Color.Red),
                shape = RoundedCornerShape(6.dp),
            )
            .clip(shape = RoundedCornerShape(6.dp)) // Make sure there is no background outside the border
            .background(color = Color.hsv(0.0f, 1.0f, 1.0f, 0.3f))
            .padding(8.dp)
    ) {
        Text(text = "ERROR")
        Text(text = message)
    }
}

@Preview
@Composable
fun PreviewError() = Column {
    AirbornTheme(darkTheme = false) {
        Surface {
            Error("Description here", modifier = Modifier.padding(8.dp))
        }
    }
    AirbornTheme(darkTheme = true) {
        Surface {
            Error("Description here", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            color = MaterialTheme.colorScheme.background,
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }
}

fun Modifier.shadow(
    color: Color = Color.Black,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }
            frameworkPaint.color = color.toArgb()

            val leftPixel = offsetX.toPx()
            val topPixel = offsetY.toPx()
            val rightPixel = size.width + topPixel
            val bottomPixel = size.height + leftPixel

            canvas.drawRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                paint = paint,
            )
        }
    }
)

@Composable
fun MultiToggleButton(
    currentSelection: String, toggleStates: List<String>, onToggleChange: (String) -> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.secondary
    val unselectedTint = MaterialTheme.colorScheme.secondaryContainer

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(shape = RoundedCornerShape(20.dp))


    ) {
        toggleStates.forEachIndexed { _, toggleState ->
            val isSelected = currentSelection.lowercase() == toggleState.lowercase()
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if (isSelected) unselectedTint else selectedTint


            Row(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(backgroundTint)
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .toggleable(value = isSelected, enabled = true, onValueChange = { selected ->
                        if (selected) {
                            onToggleChange(toggleState)
                        }
                    })
            ) {
                Text(
                    toggleState.uppercase(), color = textColor, modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun <T> LazyCollapsible(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    header: String,
    expanded: Boolean = false,
    value: LoadingState<T>,
    onExpand: () -> Unit,
    content: @Composable ColumnScope.(T) -> Unit
) {
    var open by rememberSaveable {
        mutableStateOf(expanded)
    }
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 300, easing = LinearOutSlowInEasing
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = header, fontSize = 22.sp)
            IconButton(onClick = {
                onExpand()
                open = !open
            }) {
                Icon(
                    imageVector = if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier.size(30.dp),
                    contentDescription = if (open) "Show less" else "Show more"
                )
            }
        }
        if (open) {
            Column(
                modifier = modifier.padding(padding),
                content = {
                    when (value) {
                        is LoadingState.Success -> LazyCollapsibleContent(content = { content(value.value) })
                        is LoadingState.Loading -> LoadingScreen()
                        is LoadingState.Error -> Error(
                            "failed to load ${header}: ${value.message}",
                            modifier = Modifier.padding(16.dp, 8.dp)
                        )
                    }
                },
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary

        )
    }
}

@Composable
fun LazyCollapsibleContent(content: @Composable ColumnScope.() -> Unit) = Column(content = content)

@Composable
fun ImageComposable(uri: String, contentDescription: String, modifier: Modifier = Modifier) {
    val zoomState = rememberZoomState()
    SubcomposeAsyncImage(modifier = modifier
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
                    color = MaterialTheme.colorScheme.background,
                    strokeWidth = 1.dp
                )
            }
        },
        contentDescription = contentDescription
    )
}

@Composable
fun TableContent(isobaricData: IsobaricData) {
    LazyColumn(
        Modifier
            .padding(8.dp)
            .heightIn(min = 0.dp, max = 800.dp)
    ) {
        val column1Weight = .25f
        val column2Weight = .25f
        val column3Weight = .25f
        val column4Weight = .25f
        item {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableCell(
                    text = "Height",
                    weight = column1Weight,
                    alignment = TextAlign.Left,
                    title = true
                )
                TableCell(text = "Temp", weight = column2Weight, title = true)
                TableCell(text = "Speed", weight = column3Weight, title = true)
                TableCell(
                    text = "Direction",
                    weight = column4Weight,
                    alignment = TextAlign.Right,
                    title = true
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                color = Color.LightGray
            )
        }
        items(isobaricData.data) { data ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.height?.let {
                    TableCell(
                        text = it.formatAsFeet(),
                        weight = column1Weight,
                        alignment = TextAlign.Left,
                    )
                }
                TableCell(text = data.temperature.toString(), weight = column2Weight)
                data.windSpeed?.let { TableCell(it.formatAsKnots(), weight = column3Weight) }
                data.windFromDirection?.let {
                    IconCell(
                        text = data.windFromDirection.toString(),
                        weight = column4Weight,
                        arrangement = Arrangement.End,
                        windDirection = it
                    )
                }
            }
            HorizontalDivider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false,
) {
    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment,
    )
}

@Composable
fun RowScope.IconCell(
    text: String,
    weight: Float,
    arrangement: Arrangement.Horizontal,
    title: Boolean = false,
    windDirection: Direction
) {
    Row(
        Modifier
            .weight(weight)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement
    ) {
        Text(
            text = text,
            Modifier.padding(10.dp),
            fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        )
        RotatableArrowIcon(direction = windDirection)
    }
}
