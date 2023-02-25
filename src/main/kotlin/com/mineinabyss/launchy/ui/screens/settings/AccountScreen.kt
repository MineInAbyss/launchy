package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.ui.screens.main.buttons.LoginMicrosoftButton

@Composable
@Preview
fun AccountScreen() {
    Scaffold { paddingValues ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                Box(
                    Modifier.padding(paddingValues).padding(start = 10.dp, top = 40.dp)
                ) {
                    val lazyListState = rememberLazyListState()
                    LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                        item("account settings") {
                            LoginMicrosoftButton(true)
                        }

                    }
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(lazyListState)
                    )
                }
            }
        }
    }
}

@Composable
fun TextField(
    label: String = "",
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    icon: ImageVector? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    onValueChange: () -> Unit = {}
): TextFieldValue {
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.onPrimaryContainer
    var text by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        value = text,
        modifier = modifier,
        textStyle = textStyle,
        leadingIcon = { icon?.let { Icon(imageVector = it, "field") } },
        label = { Text(text = label) },
        singleLine = singleLine,
        placeholder = { Text(text = placeholder) },
        keyboardOptions = keyboardOptions,
        onValueChange = { newText ->
            text = newText
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = primaryColor,
            cursorColor = errorColor,
            unfocusedBorderColor = errorColor,
            unfocusedLabelColor = errorColor,
            focusedBorderColor = primaryColor,
            focusedLabelColor = primaryColor,
            placeholderColor = primaryColor
        )
    )
    return text
}
