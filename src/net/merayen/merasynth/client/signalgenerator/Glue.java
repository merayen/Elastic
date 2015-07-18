package net.merayen.merasynth.client.signalgenerator;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.Context;
import net.merayen.merasynth.glue.nodes.GlueNode;

public class Glue extends GlueNode {
	private float frequency;

	public Glue(Context context) {
		super(context);
	}

	@Override
	public String getClassPath() {
		return "net.merayen.merasynth.client.signalgenerator";
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
		createPort("output");
	}

	@Override
	protected void onDump(JSONObject state) {
		
	}

	protected void onRestore(JSONObject state) {
		frequency = ((Double)state.get("frequency")).floatValue();
		System.out.printf("PulseGenerator is getting restored! My freq: %f\n", frequency);

		ui().whenReady( () -> ui().setFrequency(frequency));
	}

	private net.merayen.merasynth.client.signalgenerator.UI ui() {
		return ((net.merayen.merasynth.client.signalgenerator.UI)this.getUINode());
	}

	public void changeFrequency(float frequency) {
		this.frequency = frequency;
		((Net)this.getNetNode()).setFrequency(frequency);
	}
}
