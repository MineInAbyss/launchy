package com.mineinabyss.launchy.auth.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.core.ui.Dialog
import com.mineinabyss.launchy.core.ui.components.LaunchyDialog
import com.mineinabyss.launchy.core.ui.dialog
import com.mineinabyss.launchy.util.DesktopHelpers

@OptIn(ExperimentalTextApi::class)
@Composable
fun AuthDialog(
    onDismissRequest: () -> Unit
) {
    val state = LocalLaunchyState
    LaunchyDialog(
        title = {
            Text("Authenticate with Microsoft", style = LocalTextStyle.current)
        },
        onAccept = { dialog = Dialog.None; onDismissRequest() },
        onDecline = { dialog = Dialog.None; onDismissRequest() },
        onDismiss = { dialog = Dialog.None; onDismissRequest() },
        acceptText = "Cancel",
        declineText = null,
    ) {
        when {
            state.profile.authCode != null -> {
                val clipboard = LocalClipboardManager.current
                val annotatedText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {

                        append("Please go to ")

                        pushUrlAnnotation(UrlAnnotation("https://microsoft.com/link"))
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                            )
                        ) {
                            append("microsoft.com/link")
                        }
                        pop()

                        append(" and enter the code ${state.profile.authCode}")
                    }
                }
                val inlineContent = mapOf(
                    // This tells the [CoreText] to replace the placeholder string "[icon]" by
                    // the composable given in the [InlineTextContent] object.
                    "copyIcon" to InlineTextContent(
                        // Placeholder tells text layout the expected size and vertical alignment of
                        // children composable.
                        Placeholder(
                            width = 12.sp,
                            height = 12.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                        )
                    ) {
                        // This Icon will fill maximum size, which is specified by the [Placeholder]
                        // above. Notice the width and height in [Placeholder] are specified in TextUnit,
                        // and are converted into pixel by text layout.

                        Icon(Icons.Filled.ContentCopy, "")
                    }
                )
                Row {
                    ClickableText(
                        annotatedText,
                        style = TextStyle.Default,
                        onClick = {
                            annotatedText.getUrlAnnotations(it, it)
                                .firstOrNull()
                                ?.let { DesktopHelpers.browse(it.item.url) }
                        },
                    )
                }
            }

            else -> Text("Getting authentiaction code...", style = LocalTextStyle.current)
        }
    }
}
