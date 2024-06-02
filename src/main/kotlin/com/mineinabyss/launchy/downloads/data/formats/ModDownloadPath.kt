package com.mineinabyss.launchy.downloads.data.formats

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.file.Path
import kotlin.io.path.Path

@Serializable(with = ValidModPathSerializer::class)
class ModDownloadPath(
    val validated: Path
)

object ValidModPathSerializer : KSerializer<ModDownloadPath> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("modPath", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): ModDownloadPath {
        val pathString = decoder.decodeString()
        if (pathString.contains("..")) error("Mod path cannot contain ..")
        val path = Path(pathString)
        if (path.isAbsolute) error("Path cannot be absolute")
        return ModDownloadPath(path)
    }

    override fun serialize(encoder: Encoder, value: ModDownloadPath) {
        encoder.encodeString(value.validated.toString())
    }

}
