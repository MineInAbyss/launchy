package com.mineinabyss.launchy.util

import kotlinx.serialization.Serializable

typealias ModName = String
typealias GroupName = String
typealias DownloadURL = String
typealias ConfigURL = String


typealias ModID = String

@Serializable
@JvmInline
value class InstanceKey(val key: String)
