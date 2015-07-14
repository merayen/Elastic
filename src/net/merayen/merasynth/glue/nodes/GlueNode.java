package net.merayen.merasynth.glue.nodes;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;

public abstract class GlueNode extends GlueObject {

	/*
	 * Handle events from the UINode this GlueNode has.
	 */
	//public static abstract class UIHandler {

		/*
		 * When user connects two ports together.
		 * Subclass can deny connection by e.g type-checking, but should then tell user why (TODO create notify-system)
		 */
	//	public boolean onConnect() {return true;} // Defaults to always allow connection

		/*
		 * When user disconnects a port
		 */
	//	public void onDisconnect() {}
	//}
	private String net_node_id; // "Soft reference" to the equivalent netnode
	private String ui_node_id; // "Soft reference" to the equivalent uinode

	public GlueNode(Context context) {
		super(context);
		//onInit();
	}

	public abstract String getClassPath();
	public abstract String getFriendlyName();
	public abstract String getDescription();

	protected void onInit() {
		/*
		 * Called once after creation.
		 */
	}

	public void doInit() {
		onInit();
	}

	public String getUINodePath() {
		return getClassPath() + ".UI";
	}

	public String getNetNodePath() {
		return getClassPath() + ".Net";
	}

	public net.merayen.merasynth.netlist.Node getNetNode() {
		assert net_node_id != null;
		return context.net_supervisor.getNodeByID(net_node_id);
	}

	public net.merayen.merasynth.ui.objects.node.Node getUINode() {
		if(ui_node_id == null)
			throw new RuntimeException("Probably too early too call");

		return context.top_ui_object.getNode(ui_node_id);
	}

	public void setNetNode(net.merayen.merasynth.netlist.Node net_node) {
		if(net_node_id != null)
			throw new RuntimeException("Net-node is already set");

		net_node_id = net_node.getID();
	}

	public void setUINode(net.merayen.merasynth.ui.objects.node.Node ui_node) {
		if(ui_node_id != null)
			throw new RuntimeException("UI-node is already set");

		ui_node_id = ui_node.getID();
	}

	public String getNetNodeID() {
		return net_node_id;
	}

	public String getUINodeID() {
		return ui_node_id;
	}

	protected void createPort(String name) {
		net.merayen.merasynth.ui.objects.node.Node ui = this.getUINode();

		ui.whenReady( () -> {
			if(ui.hasPort(name))
				return;
			ui.onCreatePort(name);
		});
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
