package com.planify.planifyspring.main.exceptions.generics

import com.planify.planifyspring.main.exceptions.ApplicationHttpException
import org.springframework.http.HttpStatus

open class AlreadyExistsHttpException(
    message: String?,
    appCode: Int = 2001,
) : ApplicationHttpException(
    httpStatus = HttpStatus.CONFLICT,
    appCode = appCode,
    message = message
)

open class BadRequestHttpException(
    message: String?,
    appCode: Int = 2005
) : ApplicationHttpException(
    httpStatus = HttpStatus.BAD_REQUEST,
    appCode = appCode,
    message = message
)

open class NotFoundHttpException(
    message: String?,
    appCode: Int = 2002
) : ApplicationHttpException(
    httpStatus = HttpStatus.NOT_FOUND,
    appCode = appCode,
    message = message
)

open class UnauthorizedHttpException(
    message: String?,
    appCode: Int = 2005
) : ApplicationHttpException(
    httpStatus = HttpStatus.UNAUTHORIZED,
    appCode = appCode,
    message = message
)

open class UnexpectedErrorHttpException(
    message: String?,
    appCode: Int = 2000
) : ApplicationHttpException(
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    appCode = appCode,
    message = message
)

open class ForbiddenHttpException(
    message: String?,
    appCode: Int = 2006
) : ApplicationHttpException(
    httpStatus = HttpStatus.FORBIDDEN,
    appCode = appCode,
    message = message
)

open class AlreadyInUseHttpException(
    message: String?,
    appCode: Int = 2011
) : ApplicationHttpException(
    httpStatus = HttpStatus.CONFLICT,
    appCode = appCode,
    message = message
)

open class ExpiredHttpException(
    message: String?,
    appCode: Int = 2012
) : ApplicationHttpException(
    httpStatus = HttpStatus.NOT_FOUND,
    appCode = appCode,
    message = message
)
