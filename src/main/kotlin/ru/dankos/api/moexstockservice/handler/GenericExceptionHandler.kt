package ru.dankos.api.moexstockservice.handler

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.dankos.api.moexstockservice.exception.StockNotFoundException
import java.util.*

@RestControllerAdvice
class GenericExceptionHandler {

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [Exception::class])
    internal fun handleAnyException(exception: Exception): ErrorResponse =
        exception.toErrorResponse(INTERNAL_SERVER_ERROR)

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(value = [StockNotFoundException::class])
    internal fun handleEntityNotFoundException(exception: StockNotFoundException): ErrorResponse =
        exception.toErrorResponse(NOT_FOUND)

    private fun Exception.toErrorResponse(httpStatus: HttpStatus): ErrorResponse = ErrorResponse(
        message = message,
        timestamp = Date(),
        errorCode = httpStatus.value(),
        errorMessage = httpStatus.name,
    )
}
