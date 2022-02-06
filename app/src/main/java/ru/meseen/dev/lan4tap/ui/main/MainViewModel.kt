package ru.meseen.dev.lan4tap.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.lanter.lan4gate.Callbacks.ICommunicationCallback
import org.lanter.lan4gate.Callbacks.INotificationCallback
import org.lanter.lan4gate.Callbacks.IResponseCallback
import org.lanter.lan4gate.Communication.CommunicationFactory
import org.lanter.lan4gate.ILan4Gate
import org.lanter.lan4gate.Lan4GateFactory
import org.lanter.lan4gate.Messages.Notification.INotification
import org.lanter.lan4gate.Messages.OperationsList
import org.lanter.lan4gate.Messages.Response.IResponse


class MainViewModel : ViewModel(),  ICommunicationCallback,
    INotificationCallback {

    private val _notification = MutableLiveData<String>()
    val notification: LiveData<String> = _notification


    private val notificationListener = NotificationListener().apply {
        callback(object : PosNotification {
            override fun send(message: String) {
                _notification.postValue(message)
            }

            override fun awaitPosFor(millis: Long) {

            }

        })
    }

    private var server = CommunicationFactory.getSingleTCPServer(20501)
    private var control = CommunicationFactory.getSizeControlDecorator(server)
    private val gate by lazy { Lan4GateFactory.getLan4Gate(DEFAULT_ECR_NUMBER, control) }

    fun connect() {
        gate.start()
        gate.addNotificationCallback(notificationListener)
        gate.addCommunicationCallback(this)
        gate.addResponseCallback { response, _ ->
            Log.v("TAGu", "newMessage: $response" )
            _notification.postValue("newMessage ${response.status}")
            response.responseText?.let {  _notification.postValue("responseText $it") }
        }
    }

    fun testHost() {
        _notification.postValue("send Test")
        val testHost = gate.getPreparedRequest(OperationsList.Test);
        testHost.ecrNumber = DEFAULT_ECR_NUMBER;
        gate.sendRequest(testHost);
    }

    fun testCommunication() {
        _notification.postValue("send TestCommunication")
        val test = gate.getPreparedRequest(OperationsList.TestCommunication)
        gate.ecrNumber = DEFAULT_ECR_NUMBER
        gate.sendRequest(test)
    }

    fun selfTest(){
        _notification.postValue("send SelfTest")
        val selfTest = gate.getPreparedRequest(OperationsList.SelfTest)
        gate.ecrNumber = DEFAULT_ECR_NUMBER
        gate.sendRequest(selfTest)
    }

    fun sale(
        price: Int,
    ) {
        _notification.postValue("send Sale")
        val sale = gate.getPreparedRequest(OperationsList.Sale).apply {
            amount = price.toLong()
            currencyCode = DEFAULT_CURRENCY_CODE
            ecrNumber = DEFAULT_ECR_NUMBER
            ecrMerchantNumber = DEFAULT_ECR_MERCHANT_NUMBER
        }
        gate.sendRequest(sale)
    }

    fun stop() {
        gate.stop()
    }

    companion object {
        private const val TAG = "MainViewModel"
        private const val DEFAULT_ECR_NUMBER = 1
        private const val DEFAULT_ECR_MERCHANT_NUMBER = 1
        private const val DEFAULT_CURRENCY_CODE = 643

    }


    override fun communicationStarted(initiator: ILan4Gate?) {
        Log.wtf(TAG, "communicationStarted: ")
        _notification.postValue("communicationStarted: ")
    }

    override fun communicationStopped(initiator: ILan4Gate?) {
        Log.wtf(TAG, "communicationStopped: ")
        _notification.postValue("communicationStopped: ")
    }

    override fun connected(initiator: ILan4Gate?) {
        Log.wtf(TAG, "connected: ")
        _notification.postValue("connected: ")
    }

    override fun disconnected(initiator: ILan4Gate?) {
        Log.wtf(TAG, "disconnected: ")
        _notification.postValue("disconnected: ")
    }

    override fun newNotificationMessage(notification: INotification?, initiator: ILan4Gate?) {
        Log.wtf(TAG, "newNotificationMessage: ")
        _notification.postValue("newNotificationMessage: ${notification?.message}")
    }


}