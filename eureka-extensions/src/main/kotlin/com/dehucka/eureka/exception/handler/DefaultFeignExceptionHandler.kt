package com.dehucka.eureka.exception.handler

import com.dehucka.eureka.exception.FeignException
import io.ktor.client.request.*
import io.ktor.client.statement.*

class DefaultFeignExceptionHandler : FeignExceptionHandler {
    override suspend fun handle(serviceName: String, request: HttpRequest, response: HttpResponse): Nothing {
        throw FeignException(serviceName, request, response.status, response.bodyAsText(Charsets.UTF_8))
    }
}