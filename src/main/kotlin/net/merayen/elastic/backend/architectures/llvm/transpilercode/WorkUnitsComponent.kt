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
	log: LogComponent,
	debug: Boolean
) : TranspilerComponent(log, debug) {
	val workUnitsMutex = PThreadMutex("work_units_mutex", log, debug)
	val workUnitsCond = PThreadCond("work_units_cond", log, debug)
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
			with(codeWriter) {
				workUnitsMutex.writeLock(codeWriter) {
					for (i in 0 until wudl.size)
						Statement("work_units[$i] = 0")

					for (node in nodes.values)
						node.nodeClass.writeCall(codeWriter, "prepare", node.instanceVariable)
				}
			}
		}
	}

	fun writeHeaders(codeWriter: CodeWriter) {
		workUnitsMutex.writeDefinition(codeWriter)
		workUnitsCond.writeDefinition(codeWriter)
	}

	fun writeDefinition(codeWriter: CodeWriter) {
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
	private fun writeWorkUnitMethods(codeWriter: CodeWriter) {
		with(codeWriter) {
			// Write headers to all the workunits
			for (index in workUnitToIndexMap.values)
				Statement("void process_workunit_$index()")

			fun writeWorkUnit(workUnit: WorkUnitDependencyList<String>.WorkUnit) {
				val dependencies = wudl[workUnit]!!
				val workUnitId = workUnitToIndexMap[workUnit]!!

				Method("void", "process_workunit_$workUnitId") {
					Comment(workUnit.nodes.toString())

					writeLog(codeWriter, "process_workunit_$workUnitId: start, %p", "process_workunit_$workUnitId")

					// If this work unit depends on more than 1 work unit, then we need to check if all of them are finished
					// If we got just 1 dependency, that dependency is already finished processing as it is the only workunit
					// queuing us.
					if (dependencies.size > 1) {
						Statement("bool all_finished = false")
						writeLock(codeWriter) {
							If("work_units[$workUnitId] == 2") {
								writeLog(codeWriter, "process_workunit_$workUnitId: already processed/processing", "")
								// Already processed. Should not happen...
								// TODO crash hardcore then...?
								//panic(codeWriter, "Already processed, should not happen")
							}
							ElseIf("work_units[$workUnitId] == 1") {
								// Ok, we are already processing, might have multiple inputs that triggers us...? Or...?
							}
							Else {
								Statement("all_finished = ${dependencies.joinToString(" && ") { "work_units[${workUnitToIndexMap[it]}] == 2" }}")
								If("all_finished") {
									Statement("work_units[$workUnitId] = 1") // Means 'we are currently processing'")
								}
							}
						}
						If("!all_finished") {
							writeLog(
								codeWriter,
								"process_workunit_$workUnitId: not all dependencies has processed (this should probably be reduced, as it adds overhead)"
							)
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
						workUnitsCond.writeCallBroadcast(codeWriter)
					}

					// Queue the nodes that are connected to us
					for (nextWorkUnit in wudl.getDependents(workUnit))
						Call("queue_task", "process_workunit_${workUnitToIndexMap[nextWorkUnit]!!}")

					writeLog(codeWriter, "process_workunit_$workUnitId: done", "")
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
							workUnitsCond.writeTimedWait(codeWriter, workUnitsMutex, 1.0, onTimeOut = {
								writeLog(codeWriter, "work_units_cond timed out!")
								//panic(codeWriter, "Timed out. Probably remove this? Though 100ms of processing is too much anyway")
								Continue()
							})

							Statement("double now")
							Block {
								Statement("struct timespec tid")
								If("clock_gettime(CLOCK_MONOTONIC_RAW, &tid)") {
									writeLog(codeWriter, "Failed to get time")
									Call("exit_failure")
								}
								Statement("now = tid.tv_sec + tid.tv_nsec / 1E9")
							}

							If("now - start >= 1.0") {
								writeLog(
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

	private fun writeDebugInfo(codeWriter: CodeWriter) {
		if (debug)
			for (number in workUnitToIndexMap.values)
				writeLog(codeWriter, "process_workunit_$number = %p", "process_workunit_$number")
	}

	/**
	 * Write a lock for work_units_cond.
	 */
	fun writeLock(codeWriter: CodeWriter, func: () -> Unit) {
		workUnitsMutex.writeLock(codeWriter, "&work_units_mutex", func)
	}
}