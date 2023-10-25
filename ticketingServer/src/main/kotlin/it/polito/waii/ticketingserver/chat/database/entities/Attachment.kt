package it.polito.waii.ticketingserver.chat.database.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant

@Entity
data class Attachment(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_seq")
    @SequenceGenerator(name = "attachment_seq", sequenceName = "attachment_seq", initialValue = 1)
    var attachmentId: Long,
    @OneToOne
    @JoinColumn(name = "chatId", nullable = false)
    var message: Chat,
    @Column(nullable=false)
    var filename: String,
    @Column(nullable=false)
    var contentType: String,
    @Column(nullable = false)
    var byteLength: Int = 0,
    @Column(nullable=false)
    var content: ByteArray = ByteArray(byteLength),
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: Timestamp = Timestamp.from(Instant.now())
)
