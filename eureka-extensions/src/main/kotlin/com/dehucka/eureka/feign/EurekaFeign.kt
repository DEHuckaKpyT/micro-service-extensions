package com.dehucka.eureka.feign

import com.dehucka.eureka.exception.handler.FeignExceptionHandler
import com.dehucka.eureka.ext.getNextServerFromEureka
import com.dehucka.microservice.ext.client
import com.netflix.discovery.EurekaClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.util.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Created on 01.09.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
abstract class EurekaFeign(
    private val serviceName: String,
    exceptionHandler: FeignExceptionHandler? = null,
) : KoinComponent {

    private val eurekaClient = get<EurekaClient>()
    private val exceptionHandler = exceptionHandler ?: get<FeignExceptionHandler>()

    suspend fun getResponse(path: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return request(path, { url -> client.prepareGet(url, block) }, { response -> response })
    }

    suspend inline fun <reified T> get(path: String, noinline block: HttpRequestBuilder.() -> Unit = {}): T {
        return get(path, typeInfo<T>(), block)
    }

    suspend fun <T> get(path: String, type: TypeInfo, block: HttpRequestBuilder.() -> Unit): T {
        return request(path, { url -> client.prepareGet(url, block) }, { response -> response.body(type) })
    }

    suspend fun postResponse(path: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        return request(path, { url -> client.preparePost(url, block) }, { response -> response })
    }

    suspend inline fun <reified T> post(path: String, noinline block: HttpRequestBuilder.() -> Unit = {}): T {
        return post(path, typeInfo<T>(), block)
    }

    suspend fun <T> post(path: String, type: TypeInfo, block: HttpRequestBuilder.() -> Unit): T {
        return request(path, { url -> client.preparePost(url, block) }, { response -> response.body(type) })
    }

    private suspend fun <T> request(
        path: String,
        clientPrepare: suspend (String) -> HttpStatement,
        returnStatement: suspend (HttpResponse) -> T
    ): T {
        val response = clientPrepare(getUrl(path)).let { statement ->
            withContext(Dispatchers.IO) { statement.execute() }
        }
        if (response.status.isSuccess()) {
            return returnStatement(response)
        } else {
            exceptionHandler.handle(serviceName, response.request, response)
        }
    }

    private fun getUrl(path: String): String {
        return eurekaClient.getNextServerFromEureka(serviceName).let { instanceInfo ->
            url {
                protocol = URLProtocol.HTTP
                host = instanceInfo.hostName
                port = instanceInfo.port
                path(path)
            }
        }
    }

    companion object
}
