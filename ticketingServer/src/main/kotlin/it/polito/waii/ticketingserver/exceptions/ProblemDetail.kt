package it.polito.waii.ticketingserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ProblemDetail {
    companion object {
        fun forStatusAndDetail(status: HttpStatus, message: String): ResponseEntity<String> {
            return ResponseEntity(message, status)
        }
    }

}

