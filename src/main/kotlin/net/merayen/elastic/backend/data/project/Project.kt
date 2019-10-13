package net.merayen.elastic.backend.data.project

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.netlist.NetList

/**
 * Holds storage, revisions and resources. Use Accessor() to retrieve and set
 * data to the Project.
 *
 * @param path The path of the project. Will create the path if it does not exist.
 */
class Project(val path: String) {
	val checkpoint = Checkpoint(this)

	/**
	 * Careful by using this directly, should probably use methods in Project().
	 */
	val data: ProjectData = ProjectData(this)

	/**
	 * Retrieve the current active NetList that can be changed.
	 * Its content will be saved when save() is called.
	 */
	val netList: NetList
		get() = data.netList

	val nodeProperties = NodeProperties(netList)

	init {
		// Initalize ProjectData here, as it need to save a checkpoint after being initialized.
		data.init()
	}

	fun save() = data.storage.createView().use { sv -> data.save(sv) }
	fun tidy() = data.tidy()
}
