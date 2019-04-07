package net.merayen.elastic.backend.data.dependencygraph;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * As usual: Not tested. Should work, but will probably not.
 */
public class Serializer {
	private Serializer() {
	}

	@SuppressWarnings("unchecked")
	public static JSONObject dump(DependencyGraph dependencyGraph) {
		dependencyGraph.tidy();

		JSONObject r = new JSONObject();

		JSONArray resources = new JSONArray();
		r.put("list", resources);

		for (DependencyItem res : dependencyGraph.getAll().values()) {
			JSONObject resource = new JSONObject();
			resources.add(resource);

			// ID
			resource.put("id", res.getId());

			// Data (key-value)
			JSONObject resource_data = new JSONObject();
			resource.put("data", resource_data);
			resource_data.putAll(res.getData());

			// Dependencies
			JSONArray depends = new JSONArray();
			resource.put("dependsOn", depends);

			for (DependencyItem rd : res.getDependsOn())
				depends.add(rd.getId());
		}

		return r;
	}

	@SuppressWarnings("unchecked")
	public static DependencyGraph load(JSONObject obj) {
		DependencyGraph dependencyGraph = new DependencyGraph();

		for (Object x : (JSONArray) obj.get("list")) {
			JSONObject r = (JSONObject) x;

			DependencyItem dependencyItem = null;

			if (r.get("id").equals(""))
				dependencyItem = dependencyGraph.get("");
			else
				dependencyItem = dependencyGraph.create((String) r.get("id"));

			// Data (key-value)
			dependencyItem.getData().putAll((JSONObject) r.get("data"));

			dependencyGraph.getAll().put(dependencyItem.getId(), dependencyItem);
		}

		for (Object x : (JSONArray) obj.get("list")) {
			JSONObject r = (JSONObject) x;
			DependencyItem dependencyItem = dependencyGraph.get((String) r.get("id"));

			for (Object y : (JSONArray) r.get("dependsOn"))
				dependencyItem.getDependsOn().add(dependencyGraph.get((String) y));
		}

		return dependencyGraph;
	}
}
