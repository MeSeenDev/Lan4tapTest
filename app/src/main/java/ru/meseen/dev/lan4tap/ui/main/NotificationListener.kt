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

    private fun setNotification(message: String, millis: Long) {
        listener?.send(message)
        listener?.awaitPosFor(millis)
    }

    override fun newNotificationMessage(notification: INotification?, initiator: ILan4Gate?) {
        if (notification != null) {
            when (notification.notificationCode) {
                NotificationsList.Connecting, NotificationsList.CardReading -> setNotification(
                    notification.message,
                    15000)
                NotificationsList.CardReadingError, NotificationsList.DeclinedCardExpired, NotificationsList.CardBlocked, NotificationsList.EjectCard, NotificationsList.AccountBlocked, NotificationsList.CardInBlackList, NotificationsList.CardReadingErrorTryAgain, NotificationsList.CardReadingErrorUseAnother, NotificationsList.CtlsDeclinedUseChip -> setNotification(
                    notification.message)
                NotificationsList.Executing, NotificationsList.TapOrInsertCard -> setNotification(
                    notification.message,
                    5000)
                NotificationsList.ReaderError, NotificationsList.ReversalUploadingError -> setNotification(
                    notification.message)
                else -> setNotification(notification.message)
            }
        }
    }

}
