package net.merayen.elastic.backend.architectures.local.utils;

import net.merayen.elastic.backend.architectures.local.LocalNode;

import java.util.Map;

/**
 * Used by combining it with UI's InputSignalParameters().
 * Transforms signal according to the properties set by that class.
 */
public class InputSignalParametersProcessor {
	private InputSignalParametersProcessor() {}

	/**
	 * Processes the audio with the set parameters.
	 */
	public static void process(LocalNode lnode, String name, float[][] input, float[][] output, int start, int length) {
		if(input.length != output.length)
			throw new RuntimeException("input and output has different channel count");

		Object p_object = lnode.getParameter("data.InputSignalParameters:" + name);

		if(p_object == null) { // No parameters. We just copy over the audio
			for(int channel = 0; channel < input.length; channel++) {
				if (start + length - start >= 0)
					System.arraycopy(input[channel], start, output[channel], start, start + length - start);
			}

		} else { // Usual business. Alter input by the parameters
			@SuppressWarnings("unchecked")
			Map<String, Object> parameters = (Map<String, Object>)p_object;

			float amplitude = ((Number)parameters.get("amplitude")).floatValue();
			float offset = ((Number)parameters.get("offset")).floatValue();

			for(int channel = 0; channel < input.length; channel++) {
				for(int i = start; i < start + length; i++) {
					output[channel][i] = input[channel][i] * amplitude + offset;
				}
			}
		}
	}
}
