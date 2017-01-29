package net.merayen.elastic.backend.storage.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ResourceManager {
	final Map<String,Resource> list = new HashMap<>();

	void add(Resource resource) {
		if(list.containsKey(resource.id) || list.containsValue(resource))
			throw new RuntimeException("Resource already exists");

		list.put(resource.id, resource);
	}

	@SuppressWarnings("unchecked")
	public <T extends Resource> T create(Class<?> cls) {
		T r;

		try {
			r = (T)cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		r.id = new Integer(UUID.randomUUID().hashCode()).toString();

		return r;
	}

	public Resource getResource(String id) {
		return list.get(id);
	}

	/**
	 * Deletes resources that has no dependencies to itself.
	 * Not tested.
	 */
	public void tidy() {
		Set<Resource> ids = new HashSet<>();
		List<Resource> resources = new ArrayList<>(list.values());

		for(Resource r : resources)
			for(Resource d : r.depends)
				ids.add(d);

		for(Resource r : resources)
			if(!ids.contains(r))
				list.remove(r);
	}
}
