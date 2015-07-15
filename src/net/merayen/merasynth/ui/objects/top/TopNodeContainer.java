package net.merayen.merasynth.ui.objects.top;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.UINet;
import net.merayen.merasynth.ui.objects.node.UINode;

public class TopNodeContainer extends UIGroup {
	/*
	 * Contains all the UINodes
	 */
	private UINet net;
	private ArrayList<UINode> nodes = new ArrayList<UINode>();

	protected void onInit() {
		net = new UINet();
		add(net, true); // Add the net first (also, drawn behind everything)
	}

	public UINode addNode(String class_path) {
		UINode node = UINode.createFromClassPath(class_path);
		nodes.add(node);
		add(node);
		return node;
	}

	public ArrayList<UINode> getNodes() {
		return new ArrayList<UINode>(nodes);
	}

	public UINode getNode(String id) {
		for(UINode x : nodes)
			if(x.getID().equals(id))
				return x;
		return null;
	}

	public void restore(JSONObject obj) {
		assert nodes.size() == 0;
		JSONArray node_dumps = (JSONArray)obj.get("nodes");

		for(int i = 0; i < node_dumps.size(); i++) {
			JSONObject node_dump = (JSONObject)node_dumps.get(i);
			UINode node = addNode((String)node_dump.get("class"));
			node.restore(node_dump);
		}
	}

	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("scroll_x", translation.scroll_x);
		result.put("scroll_y", translation.scroll_y);
		result.put("nodes", dumpUINodes());
		return result;
	}

	private JSONArray dumpUINodes() {
		JSONArray node_dumps = new JSONArray();

		for(UINode x : nodes) {
			JSONObject node_dump = x.dump();
			if(node_dump != null)
				node_dumps.add(node_dump);
		}

		return node_dumps;
	}
}
