package com.dehucka.microservice.logger

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.reflect.full.companionObject


/**
 * Created on 26.07.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
interface Logging {
    @Suppress("unused")
    val logger
        get() = cachedLoggerOf(this.javaClass)
}

fun loggerOf(ofClass: Class<*>): Logger {
    return LoggerFactory.getLogger(unwrapCompanionClass(ofClass).name) as Logger
}

fun cachedLoggerOf(ofClass: Class<*>): Logger {
    return loggerCache.getOrPut(ofClass) { loggerOf(ofClass) }
}

// unwrap companion class to enclosing class given a Java Class
private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass?.kotlin?.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

private val loggerCache = Collections.synchronizedMap(SimpleLoggerLruCache(100))

/**
 * A very simple cache for loggers, to be used with [cachedLoggerOf].
 */
private class SimpleLoggerLruCache(private val maxEntries: Int) : LinkedHashMap<Class<*>, Logger>(maxEntries, 1f) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Class<*>, Logger>): Boolean {
        return size > maxEntries
    }
}
