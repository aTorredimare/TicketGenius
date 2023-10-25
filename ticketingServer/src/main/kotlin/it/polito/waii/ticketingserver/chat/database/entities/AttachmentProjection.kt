import java.sql.Timestamp

interface AttachmentProjection{
    fun getFilename(): String
    fun getContentType(): String
    fun getByteLength(): Int
    fun getAttachmentId(): Long
    fun getTimestamp(): Timestamp
}
