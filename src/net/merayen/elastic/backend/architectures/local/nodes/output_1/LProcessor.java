package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Processor for outputting audio.
 * Accumulates audio and then the Net-node will acquire data when needed.
 */
public class LProcessor extends LocalProcessor {
	private boolean valid = true;

	@Override
	protected void onProcess() {
		// TODO output directly to interface
		Inlet inlet = getInlet("input");
		if(inlet != null) {
			AudioInlet ai = (AudioInlet)inlet;
			System.out.printf("Output LProcessor %s processing. First: %f, written: %d, inlet: %s\n", this, ai.outlet.audio[0], inlet.available(), ai);
		}
	}

	@Override
	protected void onInit() {
		System.out.println("Output LProcessor onInit()");
	}

	@Override
	protected void onPrepare() {
		// TODO Auto-generated method stub
		System.out.println("Output onPrepare()");
	}

	@Override
	protected void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
