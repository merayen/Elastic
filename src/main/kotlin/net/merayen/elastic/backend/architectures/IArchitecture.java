package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.system.intercom.backend.InitBackendMessage;

public interface IArchitecture {
	String getDescription();
	String getName();
	AbstractExecutor getExecutor(InitBackendMessage message);
}
