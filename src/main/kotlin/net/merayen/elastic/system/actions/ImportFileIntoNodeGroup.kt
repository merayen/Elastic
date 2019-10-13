package net.merayen.elastic.system.actions

import net.merayen.elastic.system.Action
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage

class ImportFileIntoNodeGroup(private val message: ImportFileIntoNodeGroupMessage) : Action() {
    override fun onMessageFromBackend(message: ElasticMessage) {}

    override fun run() {
        println("Action will be run on these files: ${message.filePaths.joinToString()}")

        TODO()

        /*environment.queue.addTask(object : QueueTask() {
            override fun onProcess() {

                val files = message.filePaths.map { File(it) }.toTypedArray()  // TODO soon: assert this actually gets run by Queue
                val result = FileConversion.convert(files)

                for (x in result.inputFileInformation) {
                    if (x is AudioFileInfo)
                        println("Converted file ${x.filePath}: ${x.duration} seconds, ${x.channels} channels, ${x.sampleRate}Hz")
                }


                for (x in result.outputFiles) {
                    val audio = environment.assertAndGetProject().data.dependencyGraph.create("audio/${x.name}")

                    //x.renameTo(File())
                    val m = environment.assertAndGetProject().data.storage.createView()
                }
                send(CreateCheckpointMessage())
            }

            override fun onCleanup() {
                println("Finished!")
            }

            override fun onCancel() {}
        })*/
    }
}