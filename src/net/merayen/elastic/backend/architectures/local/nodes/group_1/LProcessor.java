package net.merayen.elastic.backend.architectures.local.nodes.group_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {
	int session_id = -1;

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		if(session_id == -1)
			session_id = spawnSession(0);
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}

}
