package net.merayen.elastic.backend.architectures;

public enum Architecture {
	LOCAL(net.merayen.elastic.backend.architectures.local.Info.class),
	REMOTE(net.merayen.elastic.backend.architectures.remote.Info.class);

	public final IArchitecture instance;

	Architecture(Class<? extends IArchitecture> cls) {
		try {
			this.instance = cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
