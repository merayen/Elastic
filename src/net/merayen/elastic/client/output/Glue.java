package net.merayen.elastic.client.output;

import java.util.HashMap;

import org.json.simple.JSONObject;

import net.merayen.elastic.glue.Context;
import net.merayen.elastic.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Output";
	}

	@Override
	public String getDescription() {
		return "Outputs audio";
	}

	@Override
	public void onInit() {
		super.onInit();
		createPort("input");
	}

	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {

	}

	private net.merayen.elastic.client.signalgenerator.UI ui() {
		return ((net.merayen.elastic.client.signalgenerator.UI)this.getUINode());
	}

	public HashMap<String, Number> getStatistics() {
		return ((Node)getNetNode()).getStatistics();
	}
}
