package net.merayen.elastic.client.delay;

import org.json.simple.JSONObject;

import net.merayen.elastic.glue.Context;
import net.merayen.elastic.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float delay_seconds;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Delay";
	}

	@Override
	public String getDescription() {
		return "Mixes two audio sources together";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("input");
		createPort("output");
	}

	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {
		// TODO 
	}

	public void changeDelay(float seconds) {
		delay_seconds = seconds;
		((Node)this.getNetNode()).changeDelay(seconds);
	}

	private net.merayen.elastic.client.graph.UI ui() {
		return ((net.merayen.elastic.client.graph.UI)this.getUINode());
	}
}
