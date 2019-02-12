package net.merayen.elastic.backend.context.action

import net.merayen.elastic.backend.context.Action
import net.merayen.elastic.backend.conversion.AudioFileInfo
import net.merayen.elastic.backend.conversion.FileConversion
import net.merayen.elastic.backend.queue.QueueTask
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage
import java.io.File

class ImportFileIntoNodeGroupAction(private val message: ImportFileIntoNodeGroupMessage) : Action() {
    override fun run() {
        println("Action will be run on these files: ${message.filePaths.joinToString()}")

        backendContext.environment.queue.addTask(object : QueueTask() {
            override fun onProcess() {

                val files = message.filePaths.map { File(it) }.toTypedArray()
                val result = FileConversion.convert(files)

                for (x in result.inputFileInformation) {
                    if (x is AudioFileInfo)
                        println("Converted file ${x.filePath}: ${x.duration} seconds, ${x.channels} channels, ${x.sampleRate}Hz")
                }


                for (x in result.outputFiles) {
                    val audio = backendContext.environment.project.data.dependencyGraph.create("audio/${x.name}")

                    //x.renameTo(File())
                    val m = backendContext.environment.project.data.storage.createView()


                }
                backendContext.message_handler.sendToBackend(CreateCheckpointMessage())
            }

            override fun onCleanup() {
                println("Finished!")
            }

            override fun onCancel() {}
        })
    }
}