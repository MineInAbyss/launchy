package com.mineinabyss.launchy.instance_list.data

data class InstanceCardInteractions(
    val onOpen: () -> Unit,
    val onPlay: () -> Unit,
)
