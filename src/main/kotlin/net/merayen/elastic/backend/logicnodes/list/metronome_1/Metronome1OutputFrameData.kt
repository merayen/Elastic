package net.merayen.elastic.backend.logicnodes.list.metronome_1

import net.merayen.elastic.system.intercom.OutputFrameData

class Metronome1OutputFrameData(nodeId: String, val currentBeat: Int? = null, val currentDivision: Int? = null) : OutputFrameData(nodeId)