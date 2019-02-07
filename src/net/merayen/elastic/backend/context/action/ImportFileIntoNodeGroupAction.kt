package net.merayen.elastic.backend.context.action

import net.merayen.elastic.backend.context.Action
import net.merayen.elastic.backend.queue.QueueTask
import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage
import net.merayen.elastic.util.UniqueID

class ImportFileIntoNodeGroupAction(private val message: ImportFileIntoNodeGroupMessage) : Action() {
    override fun run() {
        println("Action will be run on file: ${message.filePath}")

        backendContext.environment.queue.addTask(object : QueueTask() {
            override fun onProcess() {
                val process = Runtime.getRuntime().exec(arrayOf("ffmpeg", "-i", "${message.filePath}", "-hide_banner", "-nostdin", "/tmp/${UniqueID.create()}.wav"))
                process.waitFor()
                val resultInput = String(process.inputStream.readAllBytes())
                val resultError = String(process.errorStream.readAllBytes())
                if (process.exitValue() == 0)
                    println("Task imports file ${resultInput}")
                else
                    println("Task imports file failed: ${resultError}")

            }

            override fun onCleanup() {

            }

            override fun onCancel() {}
        })
    }
}