package net.merayen.elastic.backend.architectures.remote;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.backend.architectures.IArchitecture;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;

public class Info implements IArchitecture {
	public String getDescription() {return "Running processor on an external device like a computer, Android etc, over TCP/IP. Not implemented yet";}
	public String getName() {return "Remote";}

	@Override
	public AbstractExecutor getExecutor(InitBackendMessage message) {
		return null;
	}
}
