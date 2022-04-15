package ru.meseen.dev.latura

import java.lang.Exception

class PosException(override val message: String) : Exception(message) {
}