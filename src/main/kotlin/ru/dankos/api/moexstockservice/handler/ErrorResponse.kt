package ru.dankos.api.moexstockservice.handler

import java.util.*

class ErrorResponse(
    val errorMessage: String?,
    val errorCode: Int,
    val httpStatus: String,
    val timestamp: Date
)
