package it.polito.waii.ticketingserver.chat.dtos

import it.polito.waii.ticketingserver.chat.database.entities.Attachment
import it.polito.waii.ticketingserver.chat.database.entities.Chat
import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.sql.Timestamp

data class ChatDTO (
    @field:NotNull(message = "Invalid id")
    @field:NotBlank(message = "Id is required")
    val chatId: Long?,

    @field:Past
    val timestamp: Timestamp,

    @field:NotBlank(message="No empty messages")
    val message: String,

    @field:NotBlank(message = "Sender required")
    val isSentByExpert: Boolean,

    val attachment: Attachment?,

    @field:NotNull(message="Chat message must belong to a ticket")
    val ticket: Ticket
)

data class MessageDTO(
    val id: Long?,
    val author: String,
    val sentByExpert: Boolean,
    val content: String,
    val attachmentDescription: URLAttachmentDTO?,
    val timestamp: Timestamp
)
fun Chat.toDTO(): ChatDTO {
    return ChatDTO(
        chatId,
        timestamp,
        message,
        isSentByExpert,
        attachment,
        ticket
    )
}

fun fromDTO(chatDTO: ChatDTO): Chat {
    return Chat(
        chatDTO.chatId,
        chatDTO.timestamp,
        chatDTO.message,
        chatDTO.isSentByExpert,
        chatDTO.attachment,
        chatDTO.ticket
    )
}
