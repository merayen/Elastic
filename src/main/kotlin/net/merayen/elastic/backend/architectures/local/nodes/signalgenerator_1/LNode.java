package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.signalgenerator_1.Data;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;
import net.merayen.elastic.util.math.BezierCurve;
import net.merayen.elastic.util.math.SignalBezierCurve;

import java.util.List;

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
	protected void onProcess(InputFrameData data) {
		for(LocalProcessor lp : getProcessors())
			lp.schedule();
	}

	@Override
	protected void onParameter(BaseNodeData instance) {
		Data data = (Data)instance;
		Float frequencyData = data.getFrequency();
		List<Float> curveData = data.getCurve();

		if(frequencyData != null)
			frequency = frequencyData;

		if(curveData != null)
			setCurveWave(curveData);
	}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onFinishFrame() {}

	private void setCurveWave(List<Float> points) {
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
