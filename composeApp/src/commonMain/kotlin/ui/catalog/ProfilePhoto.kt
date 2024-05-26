package ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.Url

@Composable
fun ProfilePhoto(modifier: Modifier = Modifier, displayName: String) {
    KamelImage(
        resource = asyncPainterResource(
            data = Url(
                "https://api.dicebear.com/7.x/initials/png?seed=${
                    displayName.replace(oldValue = " ", newValue = "%20")
                }&randomizeIds=true"
            )
        ),
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(20)).size(40.dp),
        onFailure = {
            it.printStackTrace()
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20)).width(40.dp)
                    .height(40.dp)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
            )
        },
        onLoading = {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20)).width(40.dp)
                    .height(40.dp)
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    )
}