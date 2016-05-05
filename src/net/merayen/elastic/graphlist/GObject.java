package net.merayen.elastic.graphlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GObject {
	GObject parent;
	final List<GObject> children = new ArrayList<>();
	public final Map<Object,Object> properties = new HashMap<>();

	GObject() {}

	@Override
	public String toString() {
		String r = "[GObject properties={";

		for(Entry<Object,Object> v : properties.entrySet()) {
			r += v.getKey().toString() + ": " + v.getValue().toString();
			r += ", ";
		}

		return r + "}]";
	}
}
