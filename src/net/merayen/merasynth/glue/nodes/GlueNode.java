package net.merayen.merasynth.glue.nodes;

import org.json.simple.JSONObject;

public abstract class GlueNode {
	/*
	 * Glue nodes that contains a netlist Node and an UI Node. These nodes are
	 * responsible for controlling both types and glue them together. Currently
	 * we want those two different node systems (UI and netlist) completely
	 * separated.
	 */
	
	protected net.merayen.merasynth.netlist.Node net_node;
	protected net.merayen.merasynth.ui.objects.node.Node ui_node;
	
	public GlueNode(net.merayen.merasynth.netlist.Node net_node, net.merayen.merasynth.ui.objects.node.Node ui_node) {
		this.net_node = net_node;
		this.ui_node = ui_node;
		onCreate();
	}
	
	public void doInit() {
		onInit();
	}
	
	public JSONObject doDump() {
		JSONObject obj = onDump();
		
		// TODO do anything with it?
		
		return obj;
	}
	
	protected void onCreate() {
		/*
		 * Called when created for the first time
		 */
	}
	
	protected void onInit() {
		/*
		 * Called when initialized (after onCreate or before onRestore)
		 */
	}
	
	protected void onRestore() {
		/*
		 * Called when restored from JSON
		 */
	}
	
	protected JSONObject onDump() { // TODO return JSON object. Really?
		/*
		 * Called when dumping to JSON
		 */
		return new JSONObject();
	}
}
