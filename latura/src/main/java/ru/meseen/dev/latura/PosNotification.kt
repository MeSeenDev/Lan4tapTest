package ru.meseen.dev.latura

import org.lanter.lan4gate.NotificationCode

data class PosNotification(
    val notification: String = "default",
    val code: NotificationCode? = null
) {
}