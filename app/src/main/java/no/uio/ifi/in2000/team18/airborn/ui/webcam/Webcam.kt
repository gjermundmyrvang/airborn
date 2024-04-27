package no.uio.ifi.in2000.team18.airborn.ui.webcam

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team18.airborn.model.Webcam
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.ImageComposable
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.LazyCollapsible

@Composable
fun WebcamSection(state: LoadingState<List<Webcam>>, initWebcam: () -> Unit) = LazyCollapsible(
    header = "Webcams", value = state, onExpand = initWebcam, padding = 0.dp
) { webcams ->
    if (webcams.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No webcams available in 20km radius", fontSize = 30.sp
            )
        }
        return@LazyCollapsible
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var selectedWebcam by rememberSaveable { mutableIntStateOf(0) }
        ImageComposable(
            uri = webcams[selectedWebcam].images.current.preview,
            contentDescription = "Webcam image",
            modifier = Modifier.aspectRatio(1920f / 1080f),
        )
        HyperlinkText(
            fullText = "Webcams provided by windy.com — add a webcam",
            linkText = listOf("windy.com", "add a webcam"),
            hyperlinks = listOf(
                "https://www.windy.com/", "https://www.windy.com/webcams/add"
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(content = {
                itemsIndexed(webcams) { index, webcam ->
                    NearbyWebcam(
                        webcam = webcam, current = webcams[selectedWebcam]
                    ) {
                        selectedWebcam = index
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            })
        }
    }
}


@Composable
fun NearbyWebcam(
    webcam: Webcam, current: Webcam, onWebcamClicked: (Webcam) -> Unit
) {
    val borderColor =
        if (webcam == current) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
    OutlinedCard(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .padding(2.dp)
            .fillMaxWidth()
            .clickable { onWebcamClicked(webcam) },
        border = BorderStroke(1.dp, color = borderColor.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(0.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .background(borderColor)
            )
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                AsyncImage(
                    model = webcam.images.current.thumbnail,
                    contentDescription = webcam.title,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = webcam.title, fontWeight = FontWeight.Bold)
                    Text(
                        text = "updated: ${webcam.lastUpdatedOn.dayNumberMonthTime} (LT)",
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    linkText: List<String>,
    linkTextColor: Color = MaterialTheme.colorScheme.secondary,
    linkTextFontWeight: FontWeight = FontWeight.Medium,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    hyperlinks: List<String>,
    fontSize: TextUnit = 12.sp
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        linkText.forEachIndexed { index, link ->
            val startIndex = fullText.indexOf(link)
            val endIndex = startIndex + link.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    fontSize = fontSize,
                    fontWeight = linkTextFontWeight,
                    textDecoration = linkTextDecoration
                ), start = startIndex, end = endIndex
            )
            addStringAnnotation(
                tag = "URL", annotation = hyperlinks[index], start = startIndex, end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontSize = fontSize, color = MaterialTheme.colorScheme.secondary
            ), start = 0, end = fullText.length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(modifier = modifier, text = annotatedString, onClick = {
        annotatedString.getStringAnnotations("URL", it, it).firstOrNull()?.let { stringAnnotation ->
            uriHandler.openUri(stringAnnotation.item)
        }
    })
}