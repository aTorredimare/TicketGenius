package it.polito.waii.ticketingserver.chat.dtos

import AttachmentProjection
import it.polito.waii.ticketingserver.chat.database.entities.Attachment
import org.springframework.core.io.ByteArrayResource
import java.sql.Timestamp

data class AttachmentDTO(
    var filename: String,
    var contentType: String,
    var byteLength: Int,
    var content: ByteArrayResource,
    var timestamp: Timestamp
)

class URLAttachmentDTO (
    var filename : String,
    var contentType: String,
    var byteLength: Int,
    var url: String,
    var timestamp: Timestamp
)

fun Attachment.toDTO(): AttachmentDTO = AttachmentDTO(
    filename,
    contentType,
    byteLength,
    ByteArrayResource(content),
    timestamp
)

fun AttachmentProjection.toURLAttachmentDTO(): URLAttachmentDTO = URLAttachmentDTO(
    getFilename(),
    getContentType(),
    getByteLength(),
    getAttachmentId().toString(),
    getTimestamp()
)
