package net.merayen.elastic.backend.architectures.local.nodes.out_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {
	AudioInlet inlet;
	LocalProcessor receiverProcessor;

	@Override
	protected void onInit() {
		Inlet inlet = getInlet("in");
		if(inlet instanceof AudioInlet)
			this.inlet = (AudioInlet)inlet;
	}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		if(inlet != null && inlet.getFormat() == Format.AUDIO && inlet.available() > 0)
			getParent().schedule(); // We have gotten data. Let's notify our parent
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}
