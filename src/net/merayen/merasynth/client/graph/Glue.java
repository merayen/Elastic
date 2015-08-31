package net.merayen.merasynth.client.graph;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float frequency;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getClassPath() { // XXX Da wut, don't we already know this?
		return "net.merayen.merasynth.client.graph";
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
		createPort("output");
	}

	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {
		// TODO 
	}

	private net.merayen.merasynth.client.graph.UI ui() {
		return ((net.merayen.merasynth.client.graph.UI)this.getUINode());
	}
}
