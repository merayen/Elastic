package net.merayen.merasynth.client.mix;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float frequency;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Mix";
	}

	@Override
	public String getDescription() {
		return "Mixes two audio sources together";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("input_a");
		createPort("input_b");
		createPort("fac");
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
