package net.merayen.elastic.system.intercom.backend

/**
 * Send this to the backend to import a file into the project.
 *
 * The response of this message is usually a CreateNodeMessage from the backend, which means a node was created in
 * response to the import.
 *
 * @param filePath The path to the file that should be imported
 * @param nodeId The id of the node where the node should be created
 * @param x The X-position of where the file got dropped
 * @param y The Y-position of where the file got dropped
 */
class ImportFileIntoNodeGroupMessage(val filePath: String, val nodeId: String, val x: Float, val y: Float) : BackendMessage()