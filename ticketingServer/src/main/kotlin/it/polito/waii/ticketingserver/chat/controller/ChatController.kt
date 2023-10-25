package it.polito.waii.ticketingserver.chat.controller

import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.chat.database.entities.Chat
import it.polito.waii.ticketingserver.chat.dtos.MessageDTO
import it.polito.waii.ticketingserver.chat.services.ChatService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@RestController
@Observed
class ChatController(
    private val chatService: ChatService,
    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private val mappingHandler: RequestMappingHandlerMapping
) {
    @GetMapping("/API/ticket/{ticketId}/chat")
    fun latest(
        @RequestParam(value = "lastMessageId", defaultValue = (-1).toString()) lastMessageId: Long,
        @PathVariable ticketId: Long
    ): ResponseEntity<List<MessageDTO>> {
        val messages = if (lastMessageId != (-1).toLong() ) {
            chatService.after(ticketId, lastMessageId)
        } else {
            chatService.latest(ticketId)
        }

        return if (messages.isNullOrEmpty()) {
            with(ResponseEntity.noContent()) {
                header("lastMessageId", lastMessageId.toString())
                build()
            }
        } else {
            with(ResponseEntity.ok()) {
                header("lastMessageId", messages.last().id.toString())
                body(messages)
            }
        }
    }

    @PostMapping("/API/ticket/newmessage")
    fun post(@RequestParam ticketId: Long, @RequestParam content: String, @RequestParam attachment: MultipartFile?) {
        chatService.post(ticketId, content, attachment)
    }

    @GetMapping("/API/ticket/{ticketId}/chat/{messageId}/{attachmentId}")
    @ResponseBody
    fun getAttachment(
        @Valid
        @PathVariable(required = true) ticketId: Long,
        @PathVariable(required = true) messageId: Chat,
        @PathVariable(required = true) attachmentId: Long,
    ): ResponseEntity<ByteArrayResource> {
        val attachmentDTO = chatService.getAttachment(ticketId, attachmentId)
        val headers = HttpHeaders()

        headers.set("content-disposition", "attachment; filename=${attachmentDTO.filename}")
        headers.contentType = MediaType.parseMediaType(attachmentDTO.contentType)
        headers["TMS-Creation-Time"] = attachmentDTO.timestamp.toString()
        return ResponseEntity.ok().headers(headers).body(attachmentDTO.content)
    }
}
