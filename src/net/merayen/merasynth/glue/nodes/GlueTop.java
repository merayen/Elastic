package net.merayen.merasynth.glue.nodes;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;

public class GlueTop extends GlueObject {
	/*
	 * Contains all other GlueNodes.
	 */
	ArrayList<GlueNode> nodes = new ArrayList<GlueNode>();

	public GlueTop(Context context) {
		super(context);
	}

	public void addObject(GlueNode object) {
		nodes.add(object);
	}

	protected void onRestore(JSONObject state) {
		if(nodes.size() > 0)
			throw new RuntimeException("Can not restore when there are existing nodes");

		JSONArray a = (JSONArray)state.get("nodes");
		for(int i = 0; i < a.size(); i++) {
			JSONObject obj = (JSONObject)a.get(i);

			GlueNode node = createNodeFromClassPath((String)obj.get("class"), context);

			node.restore(obj);

			nodes.add(node);
		}

		// Initialize all nodes after restoring
		for(GlueNode node : nodes)
			node.doInit();
	}

	protected void onDump(JSONObject state) {
		JSONArray result = new JSONArray();
		for(GlueNode c : nodes)
			result.add(c.dump());

		state.put("nodes", result);
	}

	public GlueNode getNode(String id) {
		for(GlueNode x : nodes)
			if(x.getUINodeID().equals(id))
				return x;

		return null;
	}

	public GlueNode getNodeByNetNodeID(String netnode_id) {
		for(GlueNode x : nodes)
			if(x.getNetNodeID().equals(netnode_id))
				return x;

		return null; 
	}

	public static GlueNode createNodeFromClassPath(String class_path, Context context) {
		GlueNode node;

		try {
			node = (GlueNode)(Class.forName(class_path).getConstructor(Context.class).newInstance(context));
		} catch (
			InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e
		) {
			e.printStackTrace();
			throw new RuntimeException(String.format("Error instantiating class when restoring: %s", class_path));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("Could not find class: %s", class_path));
		}

		return node;
	}
}
