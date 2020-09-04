package systems.danger

import systems.danger.cmd.dangerfile.DangerFile

object DangerKotlin {
    private const val FILE_TMP_OUTPUT_JSON = "danger_out.json"

    fun run(logger: Logger) {
        val dangerDSLPath = readLine()

        if (dangerDSLPath != null) {
            logger.info("Got Danger DSL path $dangerDSLPath", true)
        } else {
            logger.error("Didn't receive a DSL path")
        }

        dangerDSLPath?.removePrefix("danger://dsl/")?.stripEndLine()?.let {
            logger.info("Stripped DSL Path $it", true)
            with(DangerFile) {
                execute(it, FILE_TMP_OUTPUT_JSON, logger)
            }

            printResult()
        }
    }

    private fun printResult() {
        println("danger-results:/$FILE_TMP_OUTPUT_JSON")
    }

    private fun String.stripEndLine(): String {
        return replaceAfter(".json", "")
    }
}