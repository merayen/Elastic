package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.util.math.BezierCurve;
import net.merayen.elastic.util.math.SignalBezierCurve;

import java.util.List;
import java.util.Map;

/**
 * TODO change mode when frequency-port is connected/disconnected
 */
public class LNode extends LocalNode {
	float frequency = 1000f; // Only used in STANDALONE mode. This parameter is set in the UI
	float amplitude = 1f;  // Only used in STANDALONE mode. This parameter is set in the UI
	float offset;
	float[] curve_wave;
	ResamplingFactory resamplingFactory;

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		curve_wave = new float[/*sample_rate / 10*/200];
		for(int i = 0; i < curve_wave.length; i++)
			curve_wave[i] = (float)Math.sin(i / (double)curve_wave.length * Math.PI * 2) * 0.2f;

		if(resamplingFactory == null)
			resamplingFactory = new ResamplingFactory(curve_wave, sample_rate);
	}

	@Override
	protected void onProcess(Map<String, Object> data) {
		for(LocalProcessor lp : getProcessors())
			lp.schedule();
	}

	@Override
	protected void onParameter(String key, Object value) {
		if(key.equals("data.frequency"))
			frequency = ((Number)value).floatValue();

		if(key.equals("data.curve"))
			setCurveWave((List<Number>)value);
	}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onFinishFrame() {}

	private void setCurveWave(List<Number> points) {
		BezierCurve.Dot[] dots = BezierCurve.fromFlat(points);
		SignalBezierCurve.getValues(dots, curve_wave);

		normalize();

		if(resamplingFactory == null)
			resamplingFactory = new ResamplingFactory(curve_wave, sample_rate);
	}

	private void normalize() {
		double avg = 0;
		for(float x : curve_wave)
			avg += x;

		avg /= curve_wave.length;

		for(int i = 0; i < curve_wave.length; i++)
			curve_wave[i] = (float)((curve_wave[i] - avg) * 2);
	}
}
