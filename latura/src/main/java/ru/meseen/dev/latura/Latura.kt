package ru.meseen.dev.latura

import kotlin.time.Duration.Companion.seconds

class Latura(
    private val connectionTimeout: Long = 20.seconds.inWholeMicroseconds,
    private val ecrNum: Short = 1,
    private val ecrMerchantNum: Short = 1,
) : BasePOS(connectionTimeout, ecrNum, ecrMerchantNum) {

    override fun testHost() {
        addToPosLog { "Calling: testSelf" }

    }

    override fun testSelf() {
        addToPosLog { "Calling: testSelf" }

    }

    override fun openSession() {
        addToPosLog { "Calling: openSession" }

    }

    override fun closeSession() {
        addToPosLog { "Calling: closeSession" }

    }

    override fun getTransactionsJournal() {
        addToPosLog { "Calling: getTransactionsJournal" }
    }

    override fun sale(
        price: Int,
        saleTransactionId: Long,
    ) {
        addToPosLog { "Calling: sale" }

    }

    override fun cancel(
        /* saleResult: FinancialTransactionResult?,*/
    ) {
        addToPosLog { "Calling: cancel" }
    }
}