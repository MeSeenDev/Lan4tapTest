package ru.meseen.dev.latura

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import org.lanter.lan4gate.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

abstract class BasePOS(
    private val connectionTimeout: Long = 20.seconds.inWholeMicroseconds,
    private val DEFAULT_ECR_NUMBER: Short = 1,
    val DEFAULT_ECR_MERCHANT_NUMBER: Short = 1,
) : IPos {

    private val connectionListeners: MutableList<ConnectionListener> by lazy { mutableListOf() }

    val communicationCallbackFlow = callbackFlow<ConnectionStatus> {
        val connectionCallback = object : IConnectionCallback() {
            override fun newState(isConnected: Boolean) {
                trySendBlocking(if (isConnected) ConnectionStatus.Connected else ConnectionStatus.Disconnected)
            }
        }
        val connectionCallbackID = gate.addConnectionCallback(connectionCallback)
        awaitClose { gate.removeConnectionCallback(connectionCallbackID) }
    }


    val notificationCallbackFlow = callbackFlow<PosNotification> {
        val notificationCallback = object : INotificationCallback() {

            override fun newData(notification: INotificationData) {
                trySendBlocking(
                    PosNotification(
                        notification = notification.message,
                        notification.code
                    )
                )
            }
        }
        val notificationID = gate.addNotificationCallback(notificationCallback)
        awaitClose { gate.removeNotificationCallback(notificationID) }
    }


    private var notificationCallback = object : INotificationCallback() {

        override fun newData(notification: INotificationData?) {
            posScope.launch {
                addToPosLog { "notification: ${notification?.message}" }
                notification?.message?.let { connectionListeners.sendNotification(it) }
            }
        }
    }

    private var communicationJob = Job()
    private val posScope by lazy { CloseableCoroutineScope(SupervisorJob() + Dispatchers.IO) }

    private val gate by lazy {
        Lan4GateFactory.getLan4Gate(
            DEFAULT_ECR_NUMBER,
            CommunicationFactory.getSingleTcpServer()
        )
    }

    @Volatile
    protected var connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected
        private set

    @Volatile
    private var isOperationRunning: Boolean = false


    /**
     * Если не вызывать [addConnectionListener] до [prepareResources] то все упадет с законным NPE
     */
    @Throws(PosException::class)
    override fun prepareResources() = runBlocking {
        addToPosLog { "Calling: prepareResources" }
        gate.start()
        withTimeoutOrNull(connectionTimeout) {
            val repeatCount = (connectionTimeout.milliseconds.inWholeSeconds).toInt()
            repeat(repeatCount) { time ->
                delay(1000)
                connectionListeners.sendTickBeforeTimeout((connectionTimeout - (time.seconds.inWholeMilliseconds + 1000)))
                addToPosLog { "isConnected $connectionStatus" }
                if (connectionStatus == ConnectionStatus.Connected) {
                    return@withTimeoutOrNull
                }
            }
        }
        if (connectionStatus == ConnectionStatus.Disconnected) throw PosException("prepareResources Pos isNotConnected")
        /*notificationCallback.newNotificationMessage(
            PosNotification(
                "Соединение установлено"
            ), gate
        )*/
    }


    override fun freeResources() {
        addToPosLog { "Calling: freeResources" }
        gate.stop() // По доке лантера остановить менеджер необходимо с занулением, по этому и существует [_gate]
    }

    override fun terminate() {
        connectionListeners.clear()
        posScope.close()
    }


    override fun addConnectionListener(connectionListener: ConnectionListener) {
        addToPosLog { "Calling: addConnectionListener" }
        connectionListener.let(connectionListeners::add)
        gate.addNotificationCallback(notificationCallback)
        communicationJob = Job()
        posScope.launch(communicationJob) {
            communicationCallbackFlow.collectLatest { communicationStatus ->
                connectionStatus = communicationStatus
            }
        }
    }

    override fun removeConnectionListener(connectionListener: ConnectionListener) {
        addToPosLog { "Calling: removeConnectionListener" }
        connectionListeners.remove(connectionListener)
        communicationJob.cancel()
    }

    /* @Throws(IllegalStateException::class)
     internal inline fun <T : TransactionResult> runPosOperation(
         resultListener: IPos.ResultListener<T>?,
         posOperation: () -> T,
     ) {
         if (!isOperationRunning) {
             notificationCallback.newNotificationMessage(
                 BaseNotification(
                     message = "Ожидание ответа от POS",
                     notificationCode = NotificationsList.Executing
                 ), gate
             )

             isOperationRunning = true
             val result = posOperation.invoke()
             isOperationRunning = false
             resultListener?.onResult(result)
         } else {
             throw IllegalStateException("Wrong terminal state! Is State Busy = $isOperationRunning")
         }
     }*/


    companion object {
        internal const val DEFAULT_CURRENCY_CODE = 643
        internal const val DEFAULT_RESPONSE_TIME_OUT_MS = 60_000L// 1 мин
        internal const val DEFAULT_RESPONSE_TEXT_TIME_OUT =
            "Истекло время получения ответа от POS"
    }


}

interface Notification {
    fun newNotificationMessage(notification: PosNotification)
}
