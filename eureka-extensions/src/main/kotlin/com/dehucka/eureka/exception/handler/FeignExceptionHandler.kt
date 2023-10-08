package com.dehucka.eureka.exception.handler

import io.ktor.client.request.*
import io.ktor.client.statement.*


/**
 * Created on 08.10.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
interface FeignExceptionHandler {
    suspend fun handle(serviceName: String, request: HttpRequest, response: HttpResponse): Nothing
}