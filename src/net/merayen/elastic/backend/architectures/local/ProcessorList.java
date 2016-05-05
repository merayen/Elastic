package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;

public class ProcessorList {
	private List<LocalProcessor> processors = new ArrayList<>(); 

	public void add(LocalProcessor localprocessor) {
		processors.add(localprocessor);
	}
}
