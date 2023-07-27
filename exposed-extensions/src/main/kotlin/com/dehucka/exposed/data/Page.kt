package com.dehucka.exposed.data


/**
 * Created on 27.07.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
data class Page<T>(
    val items: List<T>,
    val totalItems: Long,
    val lastPage: Long
)
