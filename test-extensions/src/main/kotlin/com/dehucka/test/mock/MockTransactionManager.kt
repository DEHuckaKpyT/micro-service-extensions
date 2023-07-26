package com.dehucka.test.mock

import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

/**
 * Created on 07.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
class MockTransactionManager : TransactionManager {
    override var defaultIsolationLevel = 0
    override var defaultReadOnly = false
    override var defaultRepetitionAttempts = 0
    private val mockedDatabase: Database = mockk(relaxed = true)

    override fun bindTransactionToThread(transaction: Transaction?) {

    }

    override fun currentOrNull(): Transaction? {
        return transaction()
    }

    override fun newTransaction(isolation: Int, readOnly: Boolean, outerTransaction: Transaction?): Transaction {
        return transaction()
    }

    private fun transaction(): Transaction {
        return mockk(relaxed = true) {
            every { db } returns mockedDatabase
        }
    }

    fun apply() {
        TransactionManager.registerManager(mockedDatabase, this@MockTransactionManager)
        Database.connect({ mockk<Connection>(relaxed = true) }, null, { this@MockTransactionManager })
    }

    fun reset() {
        TransactionManager.resetCurrent(null)
        TransactionManager.closeAndUnregister(mockedDatabase)
    }
}

val manager = MockTransactionManager()
fun mockDatabase() = manager.apply()
fun unmockDatabase() = manager.reset()