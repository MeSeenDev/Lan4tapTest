package ru.meseen.dev.latura

sealed class ConnectionStatus {
    object Connected : ConnectionStatus()
    object Disconnected : ConnectionStatus()
    data class TickBeforeTimeOut(val tick: Long) : ConnectionStatus()
    object ConnectionTimeout : ConnectionStatus()
}
