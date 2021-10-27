package ru.meseen.dev.lan4tap.ui.main

interface PosNotification {
    fun send(message: String)

    fun awaitPosFor(millis: Long)
}
