package it.polito.waii.ticketingserver.chat.services

import it.polito.waii.ticketingserver.chat.dtos.AttachmentDTO
import it.polito.waii.ticketingserver.chat.dtos.ChatDTO
import it.polito.waii.ticketingserver.chat.dtos.MessageDTO
import org.springframework.web.multipart.MultipartFile

interface ChatService {
    /*fun findMessagesForTicket(ticketId: Long): List<ChatDTO>?
    fun saveNewChatMessage(chatDTO: ChatDTO): ChatDTO?*/
    fun latest(ticketId: Long): List<MessageDTO>?
    fun after(ticketId: Long, lastMessageId: Long): List<MessageDTO>?
    fun post(ticketId: Long, messageContent: String, file: MultipartFile?)
    fun getAttachment(ticketId: Long, attachmentId: Long): AttachmentDTO
}
