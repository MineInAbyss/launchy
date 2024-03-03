package com.mineinabyss.launchy.ui.auth

import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.elements.LaunchyDialog
import com.mineinabyss.launchy.ui.screens.Dialog
import com.mineinabyss.launchy.ui.screens.dialog

@Composable
fun AuthDialog(
    windowScope: WindowScope,
    onDismissRequest: () -> Unit
) {
    val state = LocalLaunchyState
    LaunchyDialog(
        title = {
            Text("Authenticate with Microsoft", style = LocalTextStyle.current)
        },
        content = {
            when {
                state.profile.authCode != null -> {
                    Text(buildAnnotatedString {
                        append("Please go to ")

                        pushStringAnnotation("link", "https://microsoft.com/link")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("microsoft.com/link")
                        }
                        pop()

                        append(" and enter the code ${state.profile.authCode}")
                    }, style = LocalTextStyle.current)
                }

                else -> Text("Getting authentiaction code...", style = LocalTextStyle.current)
            }
        },
        windowScope,
        { dialog = Dialog.None; onDismissRequest() },
        { dialog = Dialog.None; onDismissRequest() },
        "Cancel",
        null,
    )
}
