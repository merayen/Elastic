package net.merayen.elastic.backend.logicnodes.list.projectcars2_1

import net.merayen.elastic.system.intercom.InputFrameData

/**
 * @param rpm The engine rounds per minute
 * @param nm Engine torque
 * @param hp Power from engine output
 * @param running Game is running the simulation
 * @param engineOn Yes, guess what this param means
 * @param throttle Driver's throttle position, 0f - 1f
 * @param breaks Break pedal position
 * @param clutch Clutch pedal position
 */
class ProjectCars2UDPData(
	nodeId: String,
	val rpm: Float,
	val nm: Float,
	val hp: Float,
	val running: Boolean,
	val engineOn: Boolean,
	val throttle: Float,
	val breaks: Float,
	val clutch: Float,
) : InputFrameData(nodeId)