package net.merayen.elastic.backend.data.resource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * As usual: Not tested. Should work, but will probably not.
 */
public class Serializer {
	private Serializer() {}

	@SuppressWarnings("unchecked")
	public static JSONObject dump(ResourceManager rm) {
		rm.tidy();

		JSONObject r = new JSONObject();

		JSONArray resources = new JSONArray();
		r.put("list", resources);

		for(Resource res : rm.list.values()) {
			JSONObject resource = new JSONObject();
			resources.add(resource);

			// ID
			resource.put("id", res.id);

			// Data (key-value)
			JSONObject resource_data = new JSONObject();
			resource.put("data", resource_data);
			resource_data.putAll(res.data);

			// Dependencies
			JSONArray depends = new JSONArray();
			resource.put("depends", depends);

			for(Resource rd : res.depends)
				depends.add(rd.id);
		}

		return r;
	}

	@SuppressWarnings("unchecked")
	public static ResourceManager load(JSONObject obj) {
		ResourceManager rm = new ResourceManager();

		for(Object x : (JSONArray)obj.get("list")) {
			JSONObject r = (JSONObject)x;
			Resource resource = new Resource();

			// ID
			resource.id = (String)r.get("id");

			// Data (key-value)
			resource.data.putAll((JSONObject)r.get("data"));

			rm.list.put(resource.id, resource);
		}

		for(Object x : (JSONArray)obj.get("list")) {
			JSONObject r = (JSONObject)x;
			Resource resource = rm.list.get(r.get("id"));

			for(Object y : (JSONArray)r.get("depends"))
				resource.depends.add(rm.list.get((String)y));
		}

		return rm;
	}
}
