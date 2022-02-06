package ru.meseen.dev.lan4tap.ui.main

import org.lanter.lan4gate.Callbacks.INotificationCallback
import org.lanter.lan4gate.ILan4Gate
import org.lanter.lan4gate.Messages.Notification.INotification
import org.lanter.lan4gate.Messages.Notification.NotificationsList

/**
 * @author Vyacheslav Doroshenko
 */
class NotificationListener : INotificationCallback {


    var listener: PosNotification? = null

    fun callback(listener: PosNotification?) {
        this.listener = listener
    }

    private fun setNotification(message: String) {
        listener?.send(message)
    }

    override fun newNotificationMessage(notification: INotification?, initiator: ILan4Gate?) {
        notification?.message?.let { setNotification(it) }
    }

}
