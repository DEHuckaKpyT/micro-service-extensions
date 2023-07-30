package com.dehucka.exposed.ext

import com.dehucka.exposed.data.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


/**
 * Created on 25.07.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
suspend inline fun <T> read(crossinline block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 0, readOnly = true) {
            block()
        }
    }

suspend inline fun <R, Arg> read(argument: Arg, crossinline block: Arg.() -> R): R =
    withContext(Dispatchers.IO) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 0, readOnly = true) {
            argument.block()
        }
    }

suspend inline fun <T> execute(crossinline block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 0, readOnly = false) {
            block()
        }
    }

suspend inline fun <R, Arg> execute(argument: Arg, crossinline block: Arg.() -> R): R =
    withContext(Dispatchers.IO) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, repetitionAttempts = 0, readOnly = false) {
            argument.block()
        }
    }

suspend inline fun <T> executeStrictly(crossinline block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction(Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 0, readOnly = false) {
            block()
        }
    }

suspend inline fun <R, Arg> executeStrictly(argument: Arg, crossinline block: Arg.() -> R): R =
    withContext(Dispatchers.IO) {
        transaction(Connection.TRANSACTION_SERIALIZABLE, repetitionAttempts = 0, readOnly = false) {
            argument.block()
        }
    }

fun <T> SizedIterable<T>.toPage(page: Long, size: Int): Page<T> {
    val offset = page * size
    val items = limit(size, offset).toList()

    val totalItems = count()
    val lastPage = if (size != 0) totalItems / size else 0

    return Page(items, totalItems, lastPage)
}

class InsensitiveLikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun <T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> =
    InsensitiveLikeOp(this, QueryParameter(pattern, columnType))

/**
 * Example:
 * val item = ...
 * MyTable.upsert {
 * 	it[id] = item.id
 *	it[value1] = item.value1
 * }
 */

fun <T : Table> T.upsert(
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(InsertStatement<Number>) -> Unit
) =
    InsertOrUpdate<Number>(this, keys = keys).apply {
        body(this)
        execute(TransactionManager.current())
    }

class InsertOrUpdate<Key : Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}

/**
 * Example:
 * val items = listOf(...)
 * MyTable.batchUpsert(items) { table, item  ->
 * 	table[id] = item.id
 *	table[value1] = item.value1
 * }
 */

fun <T : Table, E> T.batchUpsert(
    data: Collection<E>,
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(BatchInsertStatement, E) -> Unit
) =
    BatchInsertOrUpdate(this, keys = keys).apply {
        data.forEach {
            addBatch()
            body(this, it)
        }
        execute(TransactionManager.current())
    }

class BatchInsertOrUpdate(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : BatchInsertStatement(table, isIgnore, shouldReturnGeneratedValues = false) {
    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}