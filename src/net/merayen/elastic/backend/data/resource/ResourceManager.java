package net.merayen.elastic.backend.data.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of resources and their dependencies.
 * Note: It is not possible to directly delete resource. You will need to clear dependencies
 * to have the function tidy() to actually delete resources. This is by design.
 */
public class ResourceManager {
	final Map<String,Resource> list = new HashMap<>();

	public ResourceManager() {
		create(""); // Create "top"-object that is permanent. Never to be deleted
	}

	public synchronized Resource create(String id) {
		if(list.containsKey(id))
			throw new RuntimeException("Resource already exists");

		Resource r = new Resource(id);

		list.put(r.id, r);

		return r;
	}

	public synchronized Resource get(String id) {
		return list.get(id);
	}

	public Resource getTop() {
		return list.get("");
	}

	public synchronized Map<String, Resource> getAll() {
		return new HashMap<>(list);
	}

	/**
	 * Deletes resources that has no dependencies to itself.
	 * Not tested.
	 */
	public synchronized void tidy() {
		Set<Resource> active = getActive();

		Iterator<Map.Entry<String,Resource>> iter = list.entrySet().iterator();

		while(iter.hasNext()) {
			Map.Entry<String,Resource> x = iter.next();
			if(!active.contains(x.getValue()))
				iter.remove();
		}
	}

	private Set<Resource> getActive() {
		Set<Resource> active = new HashSet<>();
		List<Resource> to_check = new ArrayList<>();
		active.add(list.get(""));
		to_check.add(list.get(""));

		while(!to_check.isEmpty()) {
			for(Resource r : to_check.remove(0).depends) {
				if(!active.contains(r) && !to_check.contains(r)) {
					to_check.add(r);
					active.add(r);
				}
			}
		}

		return active;
	}
}
