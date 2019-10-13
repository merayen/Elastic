package net.merayen.elastic.system.actions;

import net.merayen.elastic.system.Action;
import net.merayen.elastic.system.intercom.ElasticMessage;
import org.jetbrains.annotations.NotNull;

public class ImportNode extends Action {

	public ImportNode() {
		// ...
	}

	@Override
	public void run() {

	}

	@Override
	public void onMessageFromBackend(@NotNull ElasticMessage message) {}
}
