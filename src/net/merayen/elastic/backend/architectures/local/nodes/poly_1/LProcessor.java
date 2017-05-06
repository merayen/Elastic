package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

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
		Inlet p = getInlet("input_0");
		if(p != null)
			p.read = p.outlet.written;
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}
