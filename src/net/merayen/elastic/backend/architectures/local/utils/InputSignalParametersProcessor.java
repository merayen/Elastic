package net.merayen.elastic.backend.architectures.local.utils;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;

/**
 * Used by combining it with UI's InputSignalParameters().
 * Transforms signal according to the properties set by that class.
 */
public class InputSignalParametersProcessor {
	private InputSignalParametersProcessor() {}

	/**
	 * Processes the audio with the set parameters.
	 * @param parameters The parameters sent by UI's InputSignalParameters()
	 * @param input Array to read from
	 * @param output Array to write to
	 */
	public static void process(LocalNode lnode, String name, float[][] input, float[][] output, int start, int length) {
		if(input.length != output.length)
			throw new RuntimeException("input and output has different channel count");

		Object p_object = lnode.getParameter("data.InputSignalParameters:" + name);

		if(p_object == null) { // No parameters. We just copy over the audio
			for(int channel = 0; channel < input.length; channel++) {
				for(int i = start; i < start + length; i++) {
					output[channel][i] = input[channel][i];
				}
			}

		} else { // Usual business. Alter input by the parameters
			@SuppressWarnings("unchecked")
			Map<String, Object> parameters = (Map<String, Object>)p_object;

			float amplitude = (float)parameters.get("amplitude");
			float offset = (float)parameters.get("offset");

			for(int channel = 0; channel < input.length; channel++) {
				for(int i = start; i < start + length; i++) {
					output[channel][i] = input[channel][i] * amplitude + offset;
				}
			}
		}
	}
}
