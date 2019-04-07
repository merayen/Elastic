package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.system.intercom.backend.InitBackendMessage;

public interface IArchitecture {
	public String getDescription();
	public String getName();
	public AbstractExecutor getExecutor(InitBackendMessage message);
}
