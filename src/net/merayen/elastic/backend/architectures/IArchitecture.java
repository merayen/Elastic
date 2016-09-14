package net.merayen.elastic.backend.architectures;

public interface IArchitecture {
	public String getDescription();
	public String getName();
	public AbstractExecutor getExecutor();
}
