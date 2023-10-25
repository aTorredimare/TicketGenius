package it.polito.waii.ticketingserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TicketingServerApplication

fun main(args: Array<String>) {
	runApplication<TicketingServerApplication>(*args)
}
