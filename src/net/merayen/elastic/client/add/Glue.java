package net.merayen.elastic.client.add;

import org.json.simple.JSONObject;

import net.merayen.elastic.glue.Context;
import net.merayen.elastic.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Math";
	}

	@Override
	public String getDescription() {
		return "Apply math to audio or data";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("output");
		createPort("input_a");
		createPort("input_b");
	}

	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {
		// TODO 
	}

	private UI ui() {
		return (UI)this.getUINode();
	}
}
