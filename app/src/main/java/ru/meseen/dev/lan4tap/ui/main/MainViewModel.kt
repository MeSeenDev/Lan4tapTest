package ru.meseen.dev.lan4tap.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.lanter.lan4gate.*


class MainViewModel : ViewModel() {

    private val _notification = MutableLiveData<String>()
    val notification: LiveData<String> = _notification

    private var notificationCallBackID: Long = -1;
    private val notificationCallBack by lazy {
        object : INotificationCallback() {
            override fun newData(notification: INotificationData?) {
                _notification.postValue("newNotificationMessage: ${notification?.message}")
            }
        }
    }
    private var connectionCallBackID: Long = -1;
    private val connectionCallBack by lazy {
        object : IConnectionCallback() {
            override fun newState(isConnected: Boolean) {
                if (isConnected) connected() else disconnected()
            }
        }
    }
    private var responseCallBackID: Long = -1;
    private val responseCallBack =
        object : IResponseCallback() {
            override fun newData(response: IResponseData?) {
                Log.v("TAGu", "newMessage: $response")
                _notification.postValue("Response:Status ${response?.status}")
                response?.responseText?.let { if (it.isNotBlank()) _notification.postValue("responseText $it") }
                response?.responseCode?.let { if (it.isNotBlank()) _notification.postValue("responseCode $it") }
            }
        }


    private val gate by lazy {
        Lan4GateFactory.getLan4Gate(DEFAULT_ECR_NUMBER,
            CommunicationFactory.getSingleTcpServer())
    }

    fun connect() {
        _notification.postValue("Start")
        notificationCallBackID = gate.addNotificationCallback(notificationCallBack)
        connectionCallBackID = gate.addConnectionCallback(connectionCallBack)
        responseCallBackID = gate.addResponseCallback(responseCallBack)
        gate.ecrNumber = DEFAULT_ECR_NUMBER
        gate.runOnThread()

    }

    fun testHost() {
        _notification.postValue("send Test")
        gate.send(OperationCode.Test)
    }

    fun testCommunication() {
        _notification.postValue("send TestCommunication")
        gate.send(OperationCode.TestCommunication)
    }

    fun selfTest() {
        _notification.postValue("send SelfTest")
        gate.send(OperationCode.SelfTest)
    }

    fun sale(
        price: Int,
    ) {
        _notification.postValue("send Sale")
        gate.apply {
            val sale = gate.getReq(OperationCode.Sale).apply {
                amount = price.toLong()
                currencyCode = DEFAULT_CURRENCY_CODE
            }
            sendMessage(sale)
        }
    }

    fun settlement() {
        gate.send(OperationCode.Settlement)
        _notification.postValue("send Settlement")
    }

    fun initialization() {
        _notification.postValue("send Initialization")
        gate.send(OperationCode.Initialization)
    }

    fun printDetailReport() {
        _notification.postValue("send PrintDetailReport")
        gate.send(OperationCode.PrintDetailReport)
    }

    private fun ILan4Gate.send(operationCode: OperationCode) {
        getPreparedRequest(operationCode).also { operation ->
            operation.ecrNumber = DEFAULT_ECR_NUMBER
            operation.ecrMerchantNumber = DEFAULT_ECR_MERCHANT_NUMBER
            sendMessage(operation)
        }
    }

    private fun ILan4Gate.getReq(operationCode: OperationCode) =
        getPreparedRequest(operationCode).apply {
            ecrMerchantNumber = DEFAULT_ECR_MERCHANT_NUMBER
            ecrNumber = DEFAULT_ECR_NUMBER
        }

    fun stop() {
        _notification.postValue("stop")
        _notification.postValue("stop:Status ${gate.stop().name}")
        if(notificationCallBackID != -1L )_notification.postValue("notificationCallBack removed ${ gate.removeNotificationCallback(notificationCallBackID)}")
        if(notificationCallBackID != -1L )_notification.postValue("connectionCallBack removed ${ gate.removeConnectionCallback(connectionCallBackID)}")
        if(notificationCallBackID != -1L )_notification.postValue("responseCallBack removed ${ gate.removeResponseCallback(responseCallBackID)}")
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val DEFAULT_ECR_NUMBER: Short = 1
        private const val DEFAULT_ECR_MERCHANT_NUMBER: Short = 1
        private const val DEFAULT_CURRENCY_CODE = "643"

    }


    fun connected() {
        Log.wtf(TAG, "connected: ")
        _notification.postValue("connection established: ")
    }

    fun disconnected() {
        Log.wtf(TAG, "disconnected: ")
        _notification.postValue("connection is lost")
    }


}