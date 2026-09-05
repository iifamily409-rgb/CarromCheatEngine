package com.potato.cheatengine

import java.io.DataOutputStream

object RootUtils {
    fun isRooted(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    fun execute(command: String): String {
        val process = Runtime.getRuntime().exec("su")
        val os = DataOutputStream(process.outputStream)
        os.writeBytes(command + "\n")
        os.flush()
        os.writeBytes("exit\n")
        os.flush()
        process.waitFor()
        return process.inputStream.bufferedReader().readText()
    }
}
