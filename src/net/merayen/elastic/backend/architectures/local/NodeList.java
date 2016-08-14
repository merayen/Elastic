package net.merayen.elastic.backend.architectures.local;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodeList {
	private final Map<String, LocalNode> list = new HashMap<>();

	NodeList() {}

	public void add(LocalNode localnode) {
		list.put(localnode.node.getID(), localnode);
	}

	public LocalNode get(String id) {
		return list.get(id);
	}

	public Set<String> keySet() {
		return list.keySet();
	}

	public Collection<LocalNode> values() {
		return list.values();
	}
}
