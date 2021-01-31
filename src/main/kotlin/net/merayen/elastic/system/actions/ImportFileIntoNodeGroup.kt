package net.merayen.elastic.system.actions

import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.backend.conversion.AudioFileInfo
import net.merayen.elastic.backend.conversion.FileConversion
import net.merayen.elastic.backend.queue.QueueTask
import net.merayen.elastic.system.Action
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage
import java.io.File

class ImportFileIntoNodeGroup(private val environment: JavaBackend.Environment, private val message: ImportFileIntoNodeGroupMessage) : Action() {
    override fun onMessageFromBackend(message: ElasticMessage) {}

    override fun run() {
        println("Action will be run on these files: ${message.filePaths.joinToString()}")

        environment.queue.addTask(object : QueueTask() {
            override fun onProcess() {

                val files = message.filePaths.map { File(it) }.toTypedArray()  // TODO soon: assert this actually gets run by Queue
                val result = FileConversion.convert(files)

                for (x in result.inputFileInformation) {
                    if (x is AudioFileInfo)
                        println("Converted file ${x.filePath}: ${x.duration} seconds, ${x.channels} channels, ${x.sampleRate}Hz")
                }

                for (x in result.outputFiles) {
                    val audio = environment.project.data.dependencyGraph.create("audio/${x.name}")

                    //x.renameTo(File())
                    val m = environment.project.data.storage.createView()
                }
                send(CreateCheckpointMessage())
            }

            override fun onCleanup() {
                println("Finished!")
            }

            override fun onCancel() {}
        })
    }
}