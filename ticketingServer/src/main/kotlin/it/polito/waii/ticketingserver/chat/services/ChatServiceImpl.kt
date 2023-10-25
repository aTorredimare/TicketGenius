package it.polito.waii.ticketingserver.chat.services

import it.polito.waii.ticketingserver.chat.database.entities.Attachment
import it.polito.waii.ticketingserver.chat.database.entities.Chat
import it.polito.waii.ticketingserver.chat.database.repositories.AttachmentRepository
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.chat.database.repositories.ChatRepository
import it.polito.waii.ticketingserver.chat.dtos.*
import it.polito.waii.ticketingserver.exceptions.ClosedTicketException
import it.polito.waii.ticketingserver.exceptions.ForbiddenException
import it.polito.waii.ticketingserver.exceptions.TicketNotFoundException
import it.polito.waii.ticketingserver.exceptions.AttachmentNotFoundException
import it.polito.waii.ticketingserver.profile.database.repositories.ProfileRepository
import it.polito.waii.ticketingserver.profile.dtos.toDTO
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketRepository
import it.polito.waii.ticketingserver.ticket.models.ERole
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE
import org.springframework.context.annotation.Primary
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.time.Instant

@Service
@LogInfo
@Primary
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val attachmentRepository: AttachmentRepository,
    private val ticketRepository: TicketRepository,
    private val profileRepository: ProfileRepository
): ChatService {
    /*override fun findMessagesForTicket(ticketId: Long): List<ChatDTO>? {
        val messages = chatRepository.findAllByTicketTicketId(ticketId)?.map { it.toDTO() }
        if (!messages.isNullOrEmpty()) {
            throw ChatNotFoundException("No messages found for ticket with id $ticketId")
        } else {
            return messages
        }
    }

    override fun saveNewChatMessage(@Valid chatDTO: ChatDTO): ChatDTO? {
        chatRepository.save(fromDTO(chatDTO))
        return chatDTO
    }*/
    private fun getCustomerId(mail: String): String? {
        return profileRepository.findByEmail(mail)?.toDTO()?.profileId
    }
    override fun latest(ticketId: Long): List<MessageDTO>? {
        val principal = SecurityContextHolder.getContext().authentication
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: throw TicketNotFoundException("Cannot find a chat related to a not existent ticket.")

        val reader = principal.name
        val id = getCustomerId(reader)
        if(reader != ticket.expert?.email && id != ticket.sale.customerId){
            throw ForbiddenException("Cannot access to a chat that is not yours.")
        }

        return chatRepository.findLatest(ticketId)?.map {
            val author: String = if(it.isSentByExpert) {
                ticket.expert!!.employeeId
            } else {
                ticket.sale.customerId
            }

            val attachment = attachmentRepository.findByMessage(it)?.toURLAttachmentDTO()
            MessageDTO(
                it.chatId,
                author,
                it.isSentByExpert,
                it.message,
                attachment,
                it.timestamp
            ) }?.toList()
    }
    override fun after(ticketId: Long, lastMessageId: Long): List<MessageDTO>? {
        val principal = SecurityContextHolder.getContext().authentication

        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: throw TicketNotFoundException("Cannot find a chat related to a not existent ticket.")

        val reader = principal.name
        val customerId = getCustomerId(reader)

        if(reader != ticket.expert?.email && customerId != ticket.sale.customerId){
            throw ForbiddenException("Cannot access to a chat that is not yours.")
        }

        return chatRepository.findLatest(ticketId, lastMessageId)?.map {
            val author: String = if(it.isSentByExpert) {
                ticket.expert!!.employeeId
            }else {
                ticket.sale.customerId

            }

            MessageDTO(
                it.chatId,
                author,
                it.isSentByExpert,
                it.message,
                attachmentRepository.findByMessage(it)?.toURLAttachmentDTO(),
                it.timestamp
        ) }?.toList()
    }

    override fun post(ticketId: Long, messageContent: String, file: MultipartFile?) {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: throw TicketNotFoundException("Cannot send a message related to a not found ticket.")

        val ticketStatus= ticket.statusHistory.maxByOrNull { it.timestamp }
        if(ticketStatus?.status  == ETicketStatus.CLOSED) {
            throw ClosedTicketException("Cannot send a message in a closed ticket")
        }

        val principal = SecurityContextHolder.getContext().authentication
        val isExpert = principal.authorities.any {it.authority == "ROLE_app_expert"}
        val customerId = getCustomerId(principal.name)

        if(isExpert && principal.name != ticket.expert?.email){
            throw ForbiddenException("You are authenticated with an other mail.")
        }
        if(!isExpert && customerId != null) {
            if (customerId != ticket.sale.customerId){
                throw ForbiddenException("You are authenticated with an other mail.")
            }
        }

        val now = Timestamp.from(Instant.now())
        val message = Chat(
            null,
            now,
            messageContent,
            isExpert,
            null,
            ticket
        )

        if(file != null){
            val attachment = Attachment(
                -1,
                message,
                file.originalFilename ?: "${now}_uploaded_file",
                file.contentType ?: APPLICATION_OCTET_STREAM_VALUE,
                file.size.toInt(),
                file.bytes,
                now
            )

            message.addAttachment(attachment)
            attachment.message = message

            chatRepository.save(message)
            attachmentRepository.save(attachment)
        }else{
            chatRepository.save(message)
        }
    }

    override fun getAttachment(ticketId: Long, attachmentId: Long): AttachmentDTO {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
            ?: throw TicketNotFoundException("Cannot retrieve the attachment related to a not found ticket.")

        val principal = SecurityContextHolder.getContext().authentication
        val isExpert = principal.authorities.any {it.authority == ERole.EXPERT.name}
        val customerId = getCustomerId(principal.name)
        if(isExpert && principal.name != ticket.expert?.email){
            throw ForbiddenException("You are authenticated with an other mail.")
        }
        if(!isExpert && customerId != null) {
            if (customerId != ticket.sale.customerId) {
                throw ForbiddenException("You are authenticated with an other mail.")
            }
        }

        val attachment = attachmentRepository.findByIdOrNull(attachmentId)
            ?: throw AttachmentNotFoundException("Attachment with id $attachmentId not found")

        return attachment.toDTO()
    }


}
