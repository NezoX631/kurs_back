package com.planify.planifyspring.main.exceptions.handlers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import com.planify.planifyspring.core.exceptions.AlreadyExistsAppError
import com.planify.planifyspring.core.exceptions.ApplicationException
import com.planify.planifyspring.main.common.entities.ApplicationResponse
import com.planify.planifyspring.main.exceptions.ApplicationHttpException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException


@Suppress("unused")
@RestControllerAdvice
class GlobalExceptionHandler {  // TODO: Split this into different handlers
    companion object {
        fun buildErrorResponse(
            error: Exception,
            status: HttpStatus,
            appCode: Int,
            message: String? = null,
        ): ResponseEntity<ApplicationResponse<Nothing>> {
            return ResponseEntity(
                ApplicationResponse(
                    ok = false,
                    appCode = appCode,
                    message = "[${status.value()}] ${status.reasonPhrase}: ${message ?: error.message}",
                    data = null
                ),
                status
            )
        }
    }

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        logger.error("Unexpected error occurred", e)

        return buildErrorResponse(
            error = e,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            appCode = 2000,
            message = "Unexpected error occurred"
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        val cause = e.cause

        return when (cause) {
            is InvalidFormatException -> {
                buildErrorResponse(
                    error = cause,
                    status = HttpStatus.BAD_REQUEST,
                    appCode = 2003,
                    message = "Bad request payload format: failed to parse"
                )
            }

            is MismatchedInputException, is ValueInstantiationException -> {
                val fields = cause.path.mapNotNull { it.fieldName }.joinToString(", ")
                buildErrorResponse(
                    error = cause,
                    status = HttpStatus.BAD_REQUEST,
                    appCode = 2003,
                    message = "Bad request payload: invalid fields: $fields"
                )
            }

            else -> {
                buildErrorResponse(
                    error = e,
                    status = HttpStatus.BAD_REQUEST,
                    appCode = 2003,
                    message = "Bad request payload"
                )
            }
        }
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = HttpStatus.METHOD_NOT_ALLOWED,
            appCode = 2010
        )
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        e: NoHandlerFoundException,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = HttpStatus.NOT_FOUND,
            appCode = 2008,
            message = "Route does not exists"
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = HttpStatus.BAD_REQUEST,
            appCode = 2005,
            message = "Required request parameter '${e.parameterName}' is not present"
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = HttpStatus.BAD_REQUEST,
            appCode = 2003,
            message = "Bad url or parameter argument format"
        )
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = HttpStatus.FORBIDDEN,
            appCode = 2006,
            message = "Access denied"
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = HttpStatus.BAD_REQUEST,
            appCode = 2003,
            message = e.bindingResult.fieldErrors.joinToString("; ") { it.defaultMessage ?: "Invalid value" }
        )
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationHttpException(
        e: ApplicationException,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        logger.warn("Unexpected application error occurred", e)

        return buildErrorResponse(
            error = e,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            appCode = 2000,
            message = "Internal server error"
        )
    }

    @ExceptionHandler(AlreadyExistsAppError::class)
    fun handleApplicationHttpException(
        e: AlreadyExistsAppError,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        logger.warn("Unexpected AlreadyExistsAppError occurred", e)

        return buildErrorResponse(
            error = e,
            status = HttpStatus.CONFLICT,
            appCode = 2001,
            message = "Already exists"
        )
    }

    @ExceptionHandler(ApplicationHttpException::class)
    fun handleApplicationHttpException(
        e: ApplicationHttpException,
        request: WebRequest
    ): ResponseEntity<ApplicationResponse<Nothing>> {
        return buildErrorResponse(
            error = e,
            status = e.httpStatus,
            appCode = e.appCode,
            message = e.message
        )
    }
}
