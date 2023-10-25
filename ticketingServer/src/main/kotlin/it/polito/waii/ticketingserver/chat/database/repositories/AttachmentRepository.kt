package it.polito.waii.ticketingserver.chat.database.repositories


import AttachmentProjection
import it.polito.waii.ticketingserver.chat.database.entities.Attachment
import it.polito.waii.ticketingserver.chat.database.entities.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepository: JpaRepository<Attachment, Long> {
    fun findByMessage(message: Chat): AttachmentProjection?
}
