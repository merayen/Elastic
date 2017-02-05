package net.merayen.elastic.backend.resource;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * As usual: Not tested. Should work, but will probably not.
 */
public class Serializer {
	private static final String path = "net.merayen.elastic.backend.storage.resource.types.%s";
	private Serializer() {}

	@SuppressWarnings("unchecked")
	public static JSONObject dump(ResourceManager rm) {
		rm.tidy();

		JSONObject r = new JSONObject();

		JSONArray resources = new JSONArray();
		r.put("list", resources);

		for(Map.Entry<String, Resource> x : rm.list.entrySet()) {
			JSONObject resource = new JSONObject();
			resources.add(resource);

			// ID
			resource.put("id", x.getValue().id);

			// Type
			resource.put("type", x.getValue().getClass().getSimpleName());

			// Data (key-value)
			JSONObject resource_data = new JSONObject();
			resource.put("data", resource_data);
			resource_data.putAll(x.getValue().data);

			// Dependencies
			JSONArray depends = new JSONArray();
			resource.put("depends", depends);
			depends.addAll(x.getValue().depends);
		}

		return r;
	}

	@SuppressWarnings("unchecked")
	public static ResourceManager load(JSONObject obj) {
		ResourceManager rm = new ResourceManager();

		for(Object x : (JSONArray)obj.get("list")) {
			JSONObject r = (JSONObject)x;
			Resource resource;

			try {
				resource = (Resource)Class.forName(String.format(path, r.get("type"))).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}

			// ID
			resource.id = (String)r.get("id");

			// Data (key-value)
			resource.data.putAll((JSONObject)r.get("data"));

			// Dependencies
			resource.depends.addAll((JSONArray)r.get("depends"));

			rm.add(resource);
		}

		return rm;
	}
}
