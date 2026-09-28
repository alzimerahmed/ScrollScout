package com.therxmv.dirolreader.ui.news.view.post.attachment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.therxmv.dirolreader.domain.models.LocationModel
import com.therxmv.dirolreader.feed.presentation.R

/**
 * Location/venue card. Tap opens the coordinates in an installed map app via a
 * `geo:` URI — no Google Maps SDK (FOSS / no-GMS constraint).
 */
@Composable
fun PostLocation(location: LocationModel) {
    val context = LocalContext.current
    val openMapLabel = stringResource(id = R.string.feed_location_open_map)
    val noAppMessage = stringResource(id = R.string.feed_attachment_no_app)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { openLocationInMaps(context, location, noAppMessage) }
            .semantics { contentDescription = openMapLabel }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.location_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = location.title ?: stringResource(
                    id = if (location.isLive) {
                        R.string.feed_location_live
                    } else {
                        R.string.feed_location_title
                    },
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = location.address ?: "${location.latitude}, ${location.longitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun openLocationInMaps(context: Context, location: LocationModel, noAppMessage: String) {
    val label = location.title ?: location.address
    val uri = if (label.isNullOrBlank()) {
        Uri.parse(location.geoUri)
    } else {
        Uri.parse("${location.geoUri}?q=${location.latitude},${location.longitude}(${Uri.encode(label)})")
    }

    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (ignored: ActivityNotFoundException) {
        Toast.makeText(context, noAppMessage, Toast.LENGTH_SHORT).show()
    }
}

@Preview
@Composable
private fun PostLocationPreview() {
    PostLocation(
        location = LocationModel(
            latitude = 40.7128,
            longitude = -74.006,
            title = null,
            address = null,
            isLive = false,
        ),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostVenuePreview() {
    PostLocation(
        location = LocationModel(
            latitude = 40.7128,
            longitude = -74.006,
            title = "Central Park",
            address = "New York, NY",
            isLive = false,
        ),
    )
}
