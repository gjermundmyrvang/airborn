package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage

@Preview
@Composable
fun TestFlightBrief() {
    FlightBrief()
}

@Composable
fun FlightBrief() {
    val dummyList: List<String> = List(5) { "Bla bla bla bla bla bla bla.  " }
    LazyColumn(
        modifier = Modifier
    ) {
        item { ImageBriefCard() }
        items(dummyList) { dummyName ->
            BriefCard(dummyName = dummyName)
        }
    }
}

@Composable
fun BriefCard(dummyName: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(
                vertical = 4.dp,
                horizontal = 4.dp
            )
    ) {
        BriefContent(dummyName)
    }
}
@Composable
fun BriefContent(dummyName: String) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "TEST",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
                )
            if (expanded) {
                Text(text = (dummyName).repeat(8))
            }
        }
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                modifier = Modifier.size(30.dp),
                contentDescription = if (expanded) "Show less" else "Show more")
        }
    }
}
@Composable
fun ImageBriefCard(url: String = "https://www.pngall.com/wp-content/uploads/10/Bar-Chart-Vector.png") {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(
                vertical = 4.dp,
                horizontal = 4.dp
            )
    ) {
        ImageBriefContent(url)
    }
}
@Composable
fun ImageBriefContent(url: String) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
        ) {
            Text(
                text = "SIGCHART",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                modifier = Modifier.size(30.dp),
                contentDescription = if (expanded) "Show less" else "Show more")
        }
    }
    if (expanded) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            SubcomposeAsyncImage(
                model = url,
                loading = {
                    CircularProgressIndicator()
                },
                contentDescription = "Image of ..."
            )
        }
    }
}
