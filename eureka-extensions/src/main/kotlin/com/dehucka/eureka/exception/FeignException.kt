package com.dehucka.eureka.exception

import io.ktor.client.request.*
import io.ktor.http.*


/**
 * Created on 08.10.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class FeignException(
    serviceName: String,
    request: HttpRequest,
    responseStatus: HttpStatusCode,
    responseBody: String,
) : RuntimeException(
    """Failed request to $serviceName:
    Request: ${request.method} ${request.url}
    Response status: $responseStatus
    Response body: $responseBody"""
)
