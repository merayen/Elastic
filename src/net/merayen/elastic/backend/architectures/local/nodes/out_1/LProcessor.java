package net.merayen.elastic.backend.architectures.local.nodes.out_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		Inlet input = getInlet("input");
		if(input != null) {
			input.read = input.outlet.written;
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}
