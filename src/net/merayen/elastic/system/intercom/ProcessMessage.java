package net.merayen.elastic.system.intercom;

import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.util.Postmaster;

/**
 * Message is sent to backend when we want to process a new frame.
 * Backend responds with a new ProcessMessage with the processed data.
 */
public class ProcessMessage extends Postmaster.Message {

	/**
	 * Format: <node-id, <data-identifier, the data>>
	 */
	public final Map<String, Map<String, Object>> data = new HashMap<>();
}
