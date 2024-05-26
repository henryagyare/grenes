package ui.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.*
import kotlin.math.absoluteValue

@Composable
fun PostCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.width(40.dp).height(40.dp)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "Display Name", style = MaterialTheme.typography.titleMedium)
                Row(
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "5h ago", style = MaterialTheme.typography.bodySmall)
                    Text(text = " • ", style = MaterialTheme.typography.bodySmall)
                    Text(text = "u/username", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Text(text = "Content", style = MaterialTheme.typography.bodyMedium)

        KamelImage(
            resource = asyncPainterResource(data = Url("https://picsum.photos/id/237/900/500")),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel() {
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f, pageCount = { 30 })
    HorizontalPager(
        state = pagerState, pageSize = PageSize.Fixed(215.dp),
        pageSpacing = 12.dp,
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 100.dp),
    ) { page ->
        Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.graphicsLayer {
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
            lerp(
                start = 0.85f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
            ).also { scale ->
                scaleX = scale
                scaleY = scale
            }

            alpha = lerp(
                start = 0.5f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )
        }) {

            val image = asyncPainterResource(data = Url("https://picsum.photos/id/237/900/500"))
            KamelImage(resource = image,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                onLoading = { progress -> CircularProgressIndicator(progress) },
                modifier = Modifier.fillMaxWidth().offset {
                    val pageOffset =
                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    IntOffset(
                        x = (70.dp * pageOffset).roundToPx(),
                        y = 0,
                    )
                })

        }
    }
}


/**
 * Linearly interpolate between [start] and [stop] with [fraction] fraction between them.
 */
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}