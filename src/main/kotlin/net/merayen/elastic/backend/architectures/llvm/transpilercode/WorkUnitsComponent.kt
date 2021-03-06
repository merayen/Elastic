package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.analyzer.node_dependency.WorkUnitDependencyList
import net.merayen.elastic.backend.analyzer.node_dependency.flattenDependencyList
import net.merayen.elastic.backend.analyzer.node_dependency.toDependencyList
import net.merayen.elastic.backend.architectures.llvm.nodes.TranspilerNode
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.netlist.NetList

/**
 * Outputs C code for the work units.
 */
class WorkUnitsComponent(
	netList: NetList,
	private val nodes: Map<String, TranspilerNode>,
	private val log: LogComponent,
	private val debug: Boolean
) {
	private val workUnitsMutex = PThreadMutex("work_units_mutex", log)
	private val workUnitsCond = PThreadCond("work_units_cond", log, debug = debug)
	private val wudl: WorkUnitDependencyList<String>

	init {
		val nodeList = toDependencyList(netList)
		flattenDependencyList(nodeList, netList)
		wudl = WorkUnitDependencyList(nodeList)
	}

	private val walk = wudl.walk()
	private val workUnitToIndexMap = walk.withIndex().map { it.value.first to it.index }.toMap()

	private fun writeWorkUnitArray(codeWriter: CodeWriter) {
		codeWriter.Statement("char work_units[${wudl.size}]")

		codeWriter.Method("void", "prepare_workunits") {
			codeWriter.Call("pthread_mutex_lock", "&work_units_mutex") // Lock the resource

			for (i in 0 until wudl.size)
				codeWriter.Statement("\twork_units[$i] = 0")

			// Unlock the resource
			codeWriter.Call("pthread_mutex_unlock", "&work_units_mutex")
		}
	}

	fun writeDefinition(codeWriter: CodeWriter) {
		workUnitsMutex.writeDefinition(codeWriter)
		workUnitsCond.writeDefinition(codeWriter)
		writeWorkUnitArray(codeWriter)
		writeWorkUnitMethods(codeWriter)
		codeWriter.Method("void", "init_workunits") {
			workUnitsMutex.writeInit(codeWriter)
			workUnitsCond.writeInit(codeWriter)
			writeDebugInfo(codeWriter)
		}
	}

	/**
	 * Write all the WorkUnit C methods
	 */
	fun writeWorkUnitMethods(codeWriter: CodeWriter) {
		with(codeWriter) {
			// Write headers to all the workunits
			for (index in workUnitToIndexMap.values)
				Statement("void process_workunit_$index()")

			fun writeWorkUnit(workUnit: WorkUnitDependencyList<String>.WorkUnit) {
				val dependencies = wudl[workUnit]!!
				val workUnitId = workUnitToIndexMap[workUnit]!!

				Method("void", "process_workunit_$workUnitId") {
					Comment(workUnit.nodes.toString())

					if (debug) log.write(codeWriter, "process_workunit_$workUnitId: start, %p", "process_workunit_$workUnitId")

					// If this work unit depends on more than 1 work unit, then we need to check if all of them are finished
					// If we got just 1 dependency, that dependency is already finished processing as it is the only workunit
					// queuing us.
					if (dependencies.size > 1) {
						Call("pthread_mutex_lock", "&work_units_mutex")
						If("work_units[$workUnitId] != 0") {
							if (debug) log.write(codeWriter, "process_workunit_$workUnitId: already processed/processing", "")
							Call("pthread_mutex_unlock", "&work_units_mutex")
							Return() // Already processed. Should not happen...
						}
						Statement("bool all_finished = ${dependencies.joinToString(" && ") { "work_units[${workUnitToIndexMap[it]}] == 2" }}")
						If("all_finished") {
							Statement("work_units[$workUnitId] = 1") // Means 'we are currently processing'")
						}

						Call("pthread_mutex_unlock", "&work_units_mutex")
						If("!all_finished") {
							if (debug) log.write(codeWriter, "process_workunit_$workUnitId: not all dependencies has processed", "")
							Return() // Need to wait for additional work units we depend on to get finished")
						}
					}

					for (node in workUnit.nodes) {
						val transpilerNode = nodes[node] ?: error("Should not happen")
						Call(transpilerNode.nodeClass.writeMethodName("process"), transpilerNode.instanceVariable)
					}

					// Write that we are finished to work_units
					// TODO only necessary if we have 2 or more dependents!
					//Call("pthread_mutex_lock", "&work_units_mutex")
					workUnitsMutex.writeLock(codeWriter, "&work_units_mutex") {
						Statement("work_units[$workUnitId] = 2")
						//workUnitsCond.writeCallBroadcast(codeWriter) // Why?

						// Queue the nodes that are connected to us
						for (nextWorkUnit in wudl.getDependents(workUnit))
							Call("queue_task", "process_workunit_${workUnitToIndexMap[nextWorkUnit]!!}")

						// Done doing exclusive things
						//Call("pthread_mutex_unlock", "&work_units_mutex")
					}

					if (debug) log.write(codeWriter, "process_workunit_$workUnitId: done", "")
				}
			}

			for ((node, _) in walk)
				writeWorkUnit(node)

			// Write the boot-method that starts processing from all the source nodes
			Method("void", "process_workunits") {
				Call("prepare_workunits")

				// Start with the source nodes
				for (workUnit in wudl.getSources())
					Call("queue_task", "process_workunit_${workUnitToIndexMap[workUnit]}")

				// Then do the independent nodes
				for (workUnit in wudl.getIndependent())
					Call("queue_task", "process_workunit_${workUnitToIndexMap[workUnit]}")

				// Wait for all work units to complete
				//Call("pthread_mutex_lock", "&work_units_mutex")
				workUnitsMutex.writeLock(codeWriter, "&work_units_mutex") {

					Statement("double start")
					Block {
						Statement("struct timespec tid")
						Statement("clock_gettime(CLOCK_MONOTONIC_RAW, &tid)")
						Statement("start = tid.tv_sec + tid.tv_nsec / 1E9")
					}

					For("int i = 0", "i < ${wudl.size}", "i++") {
						While("work_units[i] != 2") {
							workUnitsCond.writeTimedWait(codeWriter, workUnitsMutex, 0.1, onTimeOut = {
								if (debug) log.write(codeWriter, "work_units_cond timed out!")
								ohshit(codeWriter, "Timed out. Probably remove this? Though 100ms of processing is too much anyway")
							})

							Statement("double now")
							Block {
								Statement("struct timespec tid")
								If("clock_gettime(CLOCK_MONOTONIC_RAW, &tid)") {
									if (debug) log.write(codeWriter, "Failed to get time")
									Call("exit_failure")
								}
								Statement("now = tid.tv_sec + tid.tv_nsec / 1E9")
							}

							//if (debug) log.write(codeWriter, "Noes! %f", "now - start")

							If("now - start >= 1.0") {
								if (debug) log.write(
									codeWriter,
									"FATAL: Timeout when waiting for all nodes to process. process_workunit_%i never finished.",
									"i"
								)
								Call("exit", "EXIT_FAILURE")
							}
						}
					}
					//Call("pthread_mutex_unlock", "&work_units_mutex")
				}
			}
		}
	}

	fun writeDebugInfo(codeWriter: CodeWriter) {
		if (!debug) return

		for (number in workUnitToIndexMap.values)
			log.write(codeWriter, "process_workunit_$number = %p", "process_workunit_$number")
	}
}