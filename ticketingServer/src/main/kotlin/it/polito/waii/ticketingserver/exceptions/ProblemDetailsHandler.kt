package it.polito.waii.ticketingserver.exceptions

import jakarta.validation.ConstraintViolationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(e: ProductNotFoundException) = ProblemDetail
        .forStatusAndDetail( HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(DuplicateProductException::class)
    fun handleDuplicateProduct(e: DuplicateProductException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!! )
    @ExceptionHandler(DuplicateProfileException::class)
    fun handleDuplicateProfile(e: DuplicateProfileException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.CONFLICT, e.message!! )
    @ExceptionHandler(ProfileNotFoundException::class)
    fun handleProfileNotFound(e: ProfileNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(DuplicateEmployeeException::class)
    fun handleDuplicateEmployee(e: DuplicateEmployeeException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleEmployeeNotFound(e: EmployeeNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(SaleNotFoundException::class)
    fun handleSaleNotFound(e: SaleNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(DuplicateSaleException::class)
    fun handleDuplicateSale(e: DuplicateSaleException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(DuplicateTicketException::class)
    fun handleDuplicateTicket(e: DuplicateTicketException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(HistoryNotFoundException::class)
    fun handleEmptyHistory(e: HistoryNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )
    @ExceptionHandler(TicketNotFoundException::class)
    fun handleTicketNotFound(e: TicketNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!! )

    @ExceptionHandler(TicketAlreadyHaveAnExpertException::class)
    fun handleTicketAlreadyHasAnExpert(e: TicketAlreadyHaveAnExpertException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    @ExceptionHandler
    fun handleTicketWithNoSaleException(e : TicketWithNoSaleException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleIllegalArgument(e: ConstraintViolationException): HashMap<String, String?> {
        val errors = hashMapOf<String, String?>()
        //print("handleIllegalArgument")

        e.constraintViolations.forEach {
            val fieldName : String = it.propertyPath.toString()
            val errorMessage : String? = it.message
            errors[fieldName] = errorMessage
        }

        return errors
    }

    @ExceptionHandler(RoleNotFoundException::class)
    fun handleRoleNotFound(e: RoleNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(KeyCloakGenericException::class)
    fun handleKeyCloakGenericException(e: KeyCloakGenericException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    @ExceptionHandler(EmptyRoleException::class)
    fun handleEmptyRoleException(e: EmptyRoleException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    @ExceptionHandler(EmptyExpertiseAreaException::class)
    fun handleEmptyRoleException(e: EmptyExpertiseAreaException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    @ExceptionHandler(KeyCloakLoginException::class)
    fun handleKeyCloakGenericException(e: KeyCloakLoginException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message!!)
    @ExceptionHandler(ChatNotFoundException::class)
    fun handleChatNotFoundException(e: ChatNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
    @ExceptionHandler(ClosedTicketException::class)
    fun handleClosedTicketException(e: ClosedTicketException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
    @ExceptionHandler(ForbiddenException::class)
    fun handleClosedTicketException(e: ForbiddenException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.FORBIDDEN, e.message!!)
    @ExceptionHandler(AttachmentNotFoundException::class)
    fun handleAttachmentNotFoundException(e: AttachmentNotFoundException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
    @ExceptionHandler(NoSalesException::class)
    fun handleNoPurchasesException(e: NoSalesException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
    @ExceptionHandler(ExpiredWarrantyException::class)
    fun handleExpiredWarrantyException(e: ExpiredWarrantyException) = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

}
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class MethodArgumentNotValidExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleConstraintViolation(e: MethodArgumentNotValidException): HashMap<String, String?> {
        val errors = hashMapOf<String, String?>()
        print("handleConstraintViolation")
        e.bindingResult.fieldErrors.forEach {
            val fieldName : String = it.field
            val errorMessage : String? = it.defaultMessage
            errors[fieldName] = errorMessage
        }

        return errors
    }
}

class ProfileNotFoundException(message: String): Exception(message)
class DuplicateProfileException(message: String): Exception(message)
class DuplicateProductException(message: String): Exception(message)
class ProductNotFoundException(message: String): Exception(message)
class EmployeeNotFoundException(message: String): Exception(message)
class DuplicateEmployeeException(message: String): Exception(message)
class SaleNotFoundException(message: String): Exception(message)
class DuplicateSaleException(message: String): Exception(message)
class HistoryNotFoundException(message: String): Exception(message)
class TicketNotFoundException(message: String): Exception(message)
class DuplicateTicketException(message: String): Exception(message)
class TicketAlreadyHaveAnExpertException(message: String) : Exception(message)
class TicketWithNoSaleException(message: String): Exception(message)
class RoleNotFoundException(message: String): Exception(message)
class KeyCloakGenericException(message: String): Exception(message)
class EmptyRoleException(message: String): Exception(message)
class EmptyExpertiseAreaException(message: String): Exception(message)
class KeyCloakLoginException(message: String): Exception(message)
class ChatNotFoundException(message: String): Exception(message)
class ClosedTicketException(message: String): Exception(message)
class ForbiddenException(message: String): Exception(message)
class AttachmentNotFoundException(message: String): Exception(message)
class NoSalesException(message: String): Exception(message)
class ExpiredWarrantyException(message: String): Exception(message)
