package ru.meseen.dev.latura

interface IPos {
    fun testHost()

    fun testSelf()

    fun openSession()

    fun closeSession()

    fun getTransactionsJournal()

    fun sale(
        price: Int,
        saleTransactionId: Long,
    )

    fun cancel(
        /* saleResult: FinancialTransactionResult?,*/
    )


    fun prepareResources()

    fun freeResources()

    fun terminate()

    fun addConnectionListener(connectionListener: ConnectionListener)

    fun removeConnectionListener(connectionListener: ConnectionListener)
}