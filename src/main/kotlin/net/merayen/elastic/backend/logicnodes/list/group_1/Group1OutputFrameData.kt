package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.OutputFrameData

/**
 * @param currentPlayheadPosition Where the current playhead position is at
 * @param outSignal The signal from the audio out nodes, format Map&lt;(node id), FloatArray(samples)&gt;
 * @param outAudio The audio from the audio out nodes, format: Map&lt;(node id), List(channel_index)&lt;FloatArray(samples)&gt;&gt;
 * @param outMidi The midi from midi out nodes, format: Map&lt;(node id), (midi data)&gt;
 */
class Group1OutputFrameData(
	nodeId: String,
	val currentPlayheadPosition: Float,
	val currentBPM: Float,
	val outSignal: Map<String, FloatArray>,
	val outAudio: Map<String, List<FloatArray>>,
	val outMidi: Map<String, ShortArray>,
) : OutputFrameData(nodeId)