package com.planify.planifyspring.core.exceptions

class AlreadyExistsAppError(message: String) : ApplicationException(message)
class NotFoundAppError(message: String) : ApplicationException(message)
class AlreadyInUseAppError(message: String) : ApplicationException(message)
class InvalidArgumentAppError(message: String) : ApplicationException(message)
