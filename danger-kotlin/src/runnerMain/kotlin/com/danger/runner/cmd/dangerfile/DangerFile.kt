package com.danger.runner.cmd.dangerfile

import com.danger.runner.cmd.*
import kotlinx.cinterop.CPointer
import platform.posix.*

object DangerFile: DangerFileBridge {
    private const val DANGERFILE_EXTENSION = ".df.kts"
    private const val DANGERFILE = "Dangerfile" + DANGERFILE_EXTENSION

    override fun execute(inputJson: String, outputJson: String) {
        val dangerfile = dangerfileParameter(inputJson) ?: DANGERFILE

        if(!dangerfile.endsWith(DANGERFILE_EXTENSION)) {
            println("The dangerfile is not valid, it must have '$DANGERFILE_EXTENSION' as extension")
            exit(1)
        }

        Cmd().name("kotlinc").args(
            "-cp",
            "/usr/local/lib/danger/danger-kotlin.jar:/usr/local/lib/danger/danger-kotlin-kts.jar",
            "-script",
            dangerfile,
            inputJson,
            outputJson
        ).exec()
    }
}

private fun dangerfileParameter(inputJson: String): String? {
    var result: String? = null

    fopen(inputJson, "r")?.apply {
        do {
            val line = readLine(this)?.let {
                val trimmedLine = it.trim()
                if (trimmedLine.startsWith("\"dangerfile\":")) {
                    val dangerFile = trimmedLine.removePrefix("\"dangerfile\": \"").removeSuffix("\"").removeSuffix("\",")
                    result = dangerFile
                }
            }
        } while (line != null && result == null)
    }.also {
        fclose(it)
    }

    return result
}

private fun readLine(file: CPointer<FILE>): String? {
    var ch = getc(file)
    var lineBuffer: Array<Char> = arrayOf()

    while ((ch != '\n'.toInt()) && (ch != EOF)) {
        lineBuffer += ch.toChar()

        ch = getc(file)
    }

    when(lineBuffer.isEmpty()) {
        true -> return null
        false -> return lineBuffer.joinToString("")
    }
}