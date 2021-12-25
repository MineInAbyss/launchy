package com.mineinabyss.launchy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlin.io.path.Path
import kotlin.io.path.inputStream

@Serializable
data class Versions(
    val modGroups: Map<String, List<Mod>>
)

@Serializable
data class Mod(
    val name: String,
    val desc: String = "",
    val url: String = "",
)

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    val versions =
        Yaml.default.decodeFromStream(Versions.serializer(), Path("src/main/kotlin/versions.yml").inputStream())

    MaterialTheme(colors = darkColors()) {
        Scaffold {
            Box {
                val lazyListState = rememberLazyListState()
                LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                    items(versions.modGroups.toList()) { (group, mods) ->
                        var expanded by remember { mutableStateOf(false) }
                        Card(Modifier.padding(2.dp).fillMaxWidth().clickable { expanded = !expanded }) {
                            Column {
                                var groupChecked by remember { mutableStateOf(Option.DEFAULT) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    TripleSwitch(groupChecked, onSwitch = { groupChecked = it })
                                    Spacer(Modifier.width(10.dp))
                                    Text(group, Modifier.weight(1f),
                                        style = MaterialTheme.typography.h5,
                                    )
                                    Icon(Icons.Outlined.ArrowDropDown, "Show mods")
                                }
                                AnimatedVisibility(expanded) {
                                    Column {
                                        for (mod in mods) ModInfo(mod, groupChecked)
                                    }
                                }
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(lazyListState)
                )
            }
        }
    }
}

@Composable
fun ModInfo(mod: Mod, groupEnabled: Option) {
    var modEnabled by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var linkExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(Modifier.padding(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Switch(
                    enabled = groupEnabled == Option.DEFAULT,
                    checked = groupEnabled == Option.ENABLED || (modEnabled && groupEnabled != Option.DISABLED),
                    onCheckedChange = { modEnabled = !modEnabled }
                )


                Row(Modifier.weight(6f)) {
                    Text(mod.name, style = MaterialTheme.typography.h6)
                }
                AnimatedVisibility(expanded) {
                    Text(
                        mod.desc,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.alpha(ContentAlpha.medium)
                    )
                }
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .rotate(rotationState),
                    onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .rotate(rotationState),
                    onClick = { linkExpanded = !linkExpanded }) {
                    Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = "URL"
                    )
                }
            }
            AnimatedVisibility(linkExpanded) {
                Text(
                    mod.url,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.alpha(ContentAlpha.medium)
                )
            }
        }
    }
}

enum class Option {
    ENABLED, DISABLED, DEFAULT
}

@Composable
fun TripleSwitch(option: Option, onSwitch: (Option) -> Unit) {
    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TripleSwitchButton(MaterialTheme.colors.error, option, Option.DISABLED, onSwitch) {
                Icon(Icons.Outlined.Cancel, "Disabled")
            }
            TripleSwitchButton(MaterialTheme.colors.surface, option, Option.DEFAULT, onSwitch) {
                Text("/")//, "Disabled")
            }
            TripleSwitchButton(MaterialTheme.colors.primary, option, Option.ENABLED, onSwitch) {
                Icon(Icons.Outlined.Check, "Enabled")
            }
        }
    }
}

@Composable
fun TripleSwitchButton(
    enabledColor: Color,
    option: Option,
    setTo: Option,
    onSwitch: (Option) -> Unit,
    content: @Composable () -> Unit
) {
    val on = option == setTo

    val bgColor by animateColorAsState(
        if (on) enabledColor else MaterialTheme.colors.surface
    )
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = bgColor),
        shape = RectangleShape,
        onClick = { onSwitch(setTo) },
        modifier = Modifier.fillMaxHeight()
    ) {
        content()
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
