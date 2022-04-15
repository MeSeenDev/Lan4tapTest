package ru.meseen.dev.latura

import android.util.Log
import kotlinx.coroutines.*
import org.lanter.lan4gate.IRequestData
import java.io.Closeable
import kotlin.coroutines.CoroutineContext


/*internal fun IRequestData.toGateRequest(receiptType: ReceiptConverter.ReceiptType = ReceiptConverter.ReceiptType.UNDEFINED) =
    GateRequest(this, receiptType)*/


internal inline fun addToPosLog(logText: () -> String) {
    Log.d("Latura", logText())
}

internal suspend fun List<ConnectionListener>.sendTimeOut() {
    withContext(Dispatchers.IO) {
        launch {
            onEach {
                it.onConnectionTimeout()
            }
        }
    }

}

internal suspend fun List<ConnectionListener>.sendTickBeforeTimeout(tick: Long) {
    withContext(Dispatchers.IO) {
        launch {
            onEach {
                it.onTickBeforeTimeout(tick)
            }
        }
    }
}

internal suspend fun List<ConnectionListener>.sendConnected() {
    withContext(Dispatchers.IO) {
        launch {
            onEach {
                it.onConnected()
            }
        }
    }
}

internal suspend fun List<ConnectionListener>.sendDisconnected() {
    withContext(Dispatchers.IO) {
        launch {
            onEach {
                it.onDisconnected()
            }
        }
    }
}

internal suspend fun List<ConnectionListener>.sendNotification(text: String) {
    withContext(Dispatchers.IO) {
        launch {
            onEach {
                it.sendNotification(text)
            }
        }
    }
}

internal val Int.millis: Long
    get() = (this * 1000).toLong()


internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}