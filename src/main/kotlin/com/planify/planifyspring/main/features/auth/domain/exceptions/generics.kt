package com.planify.planifyspring.main.features.auth.domain.exceptions

import com.planify.planifyspring.main.exceptions.generics.UnauthorizedHttpException

class TokenInvalidHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3006,
    message = message
)

class SuspiciousActivityDetectedHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3012,
    message = message
)

class TokenExpiredHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3005,
    message = message
)

class InvalidSessionHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3010,
    message = message
)

class AuthorizationNotSpecifiedHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3001,
    message = message
)

class AuthorizationTypeUnknownHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3003,
    message = message
)


class AuthorizationTokenNotSpecifiedHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3004,
    message = message
)

class AuthorizationFailedHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3013,
    message = message
)

class UnknownUserHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3008,
    message = message
)

class InactiveSessionHttpException(
    message: String?
) : UnauthorizedHttpException(
    appCode = 3011,
    message = message
)
