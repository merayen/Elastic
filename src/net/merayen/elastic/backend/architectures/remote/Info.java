package net.merayen.elastic.backend.architectures.remote;

import net.merayen.elastic.backend.architectures.IArchitecture;

public class Info implements IArchitecture {
	public String getDescription() {return "Running processor on an external device like a computer, Android etc, over TCP/IP. Not implemented yet";}
	public String getName() {return "Remote";}
}
