package net.merayen.merasynth.glue.nodes;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;

public abstract class GlueNode extends GlueObject {
	private String net_node_id;
	private String ui_node_id;

	public GlueNode(Context context) {
		super(context);
	}

	public abstract String getUINodePath();
	public abstract String getNetNodePath();
	public abstract String getFriendlyName();
	public abstract String getDescription();

	public net.merayen.merasynth.netlist.Node getNetNode() {
		assert net_node_id != null;
		return context.net_supervisor.getNodeByID(net_node_id);
	}

	public net.merayen.merasynth.ui.objects.node.Node getUINode() {
		assert ui_node_id != null;
		return context.top_ui_object.getNode(ui_node_id);
	}

	public void setNetNode(net.merayen.merasynth.netlist.Node net_node) {
		if(net_node_id != null)
			throw new RuntimeException("UI node is already set");

		net_node_id = net_node.getID();
	}

	public void setUINode(net.merayen.merasynth.ui.objects.node.Node ui_node) {
		if(ui_node_id != null)
			throw new RuntimeException("UI node is already set");

		ui_node_id = ui_node.getID();
	}

	@Override
	public JSONObject dump() {
		JSONObject result = super.dump();
		result.put("net_node_id", net_node_id);
		result.put("ui_node_id", ui_node_id);
		return result;
	}

	@Override
	public void restore(JSONObject obj) {
		ui_node_id = (String)obj.get("ui_node_id");
		net_node_id = (String)obj.get("net_node_id");
		super.restore(obj);
	}
}
