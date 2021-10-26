package ru.meseen.dev.lan4tap.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.lanter.lan4gate.Implementation.Lan4Gate
import org.lanter.lan4gate.Messages.OperationsList

import org.lanter.lan4gate.Messages.Request.IRequest
import java.lang.IllegalStateException
import java.util.logging.Logger


class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    protected val DEFAULT_ECR_NUMBER = 1
    protected val DEFAULT_ECR_MERCHANT_NUMBER = 1
    protected val DEFAULT_CURRENCY_CODE = 643

    private val _notification = MutableLiveData<String>()
    val notification: LiveData<String> =_notification


    private val notificationListener = NotificationListener().apply {
    NotificationListener(object : PosNotification{
        override fun send(message: String?) {
            _notification.postValue(message)
        }

        override fun awaitPosFor(millis: Long) {

        }

    })
    }

    private val gate by lazy { Lan4Gate() }

    fun connect(){
        gate.addNotificationCallback(notificationListener)
        if(!gate.isStarted) gate.start()
    }

    fun sale(
        price: Int,
    ) {
        gate.getPreparedRequest(OperationsList.Sale).apply {
           amount = price.toLong()
           currencyCode = DEFAULT_CURRENCY_CODE
           ecrNumber = DEFAULT_ECR_NUMBER
           ecrMerchantNumber = DEFAULT_ECR_MERCHANT_NUMBER
           gate.sendRequest(this)
        }


    }

    fun stop(){
        gate.removeNotificationCallback(notificationListener)
        if(gate.isStarted) gate.stop()
    }


}