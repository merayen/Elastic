package net.merayen.elastic.client.adsr;

import org.json.simple.JSONObject;

import net.merayen.elastic.glue.Context;
import net.merayen.elastic.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float frequency;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Graph";
	}

	@Override
	public String getDescription() {
		return "Graph utility";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("input");
		createPort("output");
		createPort("fac");
	}

	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {
		// TODO 
	}

	private net.merayen.elastic.client.graph.UI ui() {
		return ((net.merayen.elastic.client.graph.UI)this.getUINode());
	}
}
