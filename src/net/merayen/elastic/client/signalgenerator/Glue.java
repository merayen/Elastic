package net.merayen.elastic.client.signalgenerator;

import org.json.simple.JSONObject;

import net.merayen.elastic.glue.Context;
import net.merayen.elastic.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float frequency;
	private float amplitude;
	private float offset;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getFriendlyName() {
		return "Pulse Generator 1.0";
	}

	@Override
	public String getDescription() {
		return "Simple wave generator with several different wave types.";
	}

	@Override
	protected void onInit() {
		super.onInit();
		createPort("frequency");
		createPort("amplitude");
		createPort("output");
	}

	@Override
	protected void onDump(JSONObject state) {
		state.put("frequency", frequency);
		state.put("amplitude", amplitude);
	}

	protected void onRestore(JSONObject state) {
		frequency = ((Double)state.get("frequency")).floatValue();
		amplitude = ((Double)state.get("amplitude")).floatValue();

		ui().whenReady( () -> ui().setFrequency(frequency));
		net().frequency = frequency;
		net().amplitude = amplitude;
	}

	private net.merayen.elastic.client.signalgenerator.UI ui() {
		return ((net.merayen.elastic.client.signalgenerator.UI)this.getUINode());
	}

	private net.merayen.elastic.client.signalgenerator.Net net() {
		return ((net.merayen.elastic.client.signalgenerator.Net)this.getNetNode());
	}

	public void changeFrequency(float frequency) {
		this.frequency = frequency;
		((Net)this.getNetNode()).frequency = frequency;
	}

	public void changeAmplitude(float amplitude) {
		this.amplitude = amplitude;
		((Net)getNetNode()).amplitude = amplitude;
	}

	public void changeOffset(float offset) {
		this.offset = offset;
		((Net)getNetNode()).offset = offset;
	}
}
