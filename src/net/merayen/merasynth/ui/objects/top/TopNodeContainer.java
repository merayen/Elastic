package net.merayen.merasynth.ui.objects.top;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.Net;
import net.merayen.merasynth.ui.objects.node.Node;

public class TopNodeContainer extends Group {
	/*
	 * Contains all the UINodes
	 */
	private Net net;
	private ArrayList<Node> nodes = new ArrayList<Node>();

	protected void onInit() {
		net = new Net();
		add(net, true); // Add the net first (also, drawn behind everything)
	}

	public Node addNode(String class_path) {
		Node node = createUINode(class_path);
		nodes.add(node);
		add(node);
		return node;
	}

	public ArrayList<Node> getNodes() {
		return new ArrayList<Node>(nodes);
	}

	public void restore(JSONObject obj) {
		assert nodes.size() == 0;
		JSONArray node_dumps = (JSONArray)obj.get("nodes");
		for(int i = 0; i < node_dumps.size(); i++)
			; // TODO
	}

	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("scroll_x", translation.scroll_x);
		result.put("scroll_y", translation.scroll_y);
		result.put("nodes", dumpUINodes());
		return result;
	}

	private JSONObject dumpUINodes() {
		JSONObject result = new JSONObject();
		JSONArray node_dumps = new JSONArray();

		for(Node x : nodes) {
			JSONObject node_dump = x.dump();
			if(node_dump != null)
				node_dumps.add(node_dump);
		}

		result.put("nodes", node_dumps);

		return result;
	}

	private net.merayen.merasynth.ui.objects.node.Node createUINode(String class_path) {
		net.merayen.merasynth.ui.objects.node.Node uinode;

		try {
			uinode = ((Class<net.merayen.merasynth.ui.objects.node.Node>)Class.forName(class_path)).newInstance();
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create UINode");
		}

		return uinode;
	}
}
