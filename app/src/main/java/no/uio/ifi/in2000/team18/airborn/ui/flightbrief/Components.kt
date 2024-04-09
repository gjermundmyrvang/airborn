package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

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
            .padding(8.dp)
    ) {
        Text(text = "ERROR")
        Text(text = message)
    }
}

@Preview
@Composable
fun PreviewError() {
    Surface {
        Box(modifier = Modifier
            .padding(16.dp)
            .width(500.dp)) {
            Error("Failed to load Metar/Taf: Unresolved Address")
        }
    }
}

@Composable
fun MultiToggleButton(
    currentSelection: String, toggleStates: List<String>, onToggleChange: (String) -> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.surfaceTint
    val unselectedTint = Color.Unspecified

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(shape = RoundedCornerShape(20.dp))


    ) {
        toggleStates.forEachIndexed { _, toggleState ->
            val isSelected = currentSelection.lowercase() == toggleState.lowercase()
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if (isSelected) Color.White else Color.Unspecified


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
fun <T> LoadingCollapsible(
    value: LoadingState<T>,
    header: String,
    padding: Dp = 16.dp,
    expanded: Boolean = false,
    content: @Composable ColumnScope.(T) -> Unit
) {
    when (value) {
        is LoadingState.Success -> Collapsible(
            header = header,
            padding = padding,
            expanded = expanded,
            content = { content(value.value) },
        )

        is LoadingState.Loading -> Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(header)
            CircularProgressIndicator(modifier = Modifier.padding(start = 16.dp))
        }

        is LoadingState.Error -> Error("failed to load ${header}: ${value.message}", modifier = Modifier.padding(16.dp, 8.dp))
    }
}


@Composable
fun Collapsible(
    modifier: Modifier = Modifier,
    padding: Dp = 16.dp,
    header: String,
    expanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
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
            IconButton(onClick = { open = !open }) {
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
                content = content,
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


