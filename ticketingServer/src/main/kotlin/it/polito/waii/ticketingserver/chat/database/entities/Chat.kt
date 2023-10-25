package it.polito.waii.ticketingserver.chat.database.entities

import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq")
    @SequenceGenerator(name = "chat_seq", sequenceName = "chat_seq", initialValue = 1)
    var chatId: Long?,
    var timestamp: Timestamp,
    var message: String,
    var isSentByExpert: Boolean,
    @OneToOne(cascade = [CascadeType.MERGE], mappedBy = "message")
    var attachment: Attachment?,
    @ManyToOne
    var ticket: Ticket,
) {
    fun addAttachment(attachment: Attachment) {
        attachment.message = this
        this.attachment = attachment
    }
}
