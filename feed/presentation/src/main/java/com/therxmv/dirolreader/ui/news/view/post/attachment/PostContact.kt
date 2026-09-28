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
import com.therxmv.dirolreader.domain.models.ContactModel
import com.therxmv.dirolreader.feed.presentation.R

/**
 * Contact card: display name + phone number. Tap opens the system dialer via
 * `tel:` URI (ACTION_DIAL — no CALL_PHONE permission needed).
 */
@Composable
fun PostContact(contact: ContactModel) {
    val context = LocalContext.current
    val callLabel = stringResource(id = R.string.feed_contact_call, contact.displayName)
    val noAppMessage = stringResource(id = R.string.feed_attachment_no_app)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { dialPhoneNumber(context, contact.phoneNumber, noAppMessage) }
            .semantics { contentDescription = callLabel }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.contact_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            painter = painterResource(id = R.drawable.call_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
    }
}

private fun dialPhoneNumber(context: Context, phoneNumber: String, noAppMessage: String) {
    try {
        // fromParts over parse/encode: keeps "+" intact and strips spaces.
        val uri = Uri.fromParts("tel", phoneNumber.trim(), null)
        context.startActivity(Intent(Intent.ACTION_DIAL, uri))
    } catch (ignored: ActivityNotFoundException) {
        Toast.makeText(context, noAppMessage, Toast.LENGTH_SHORT).show()
    }
}

@Preview
@Composable
private fun PostContactPreview() {
    PostContact(
        contact = ContactModel(
            firstName = "Jane",
            lastName = "Doe",
            phoneNumber = "+1 555 0100",
            userId = 0L,
        ),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostContactDarkPreview() {
    PostContact(
        contact = ContactModel(
            firstName = "Jane",
            lastName = "",
            phoneNumber = "+1 555 0100",
            userId = 0L,
        ),
    )
}
