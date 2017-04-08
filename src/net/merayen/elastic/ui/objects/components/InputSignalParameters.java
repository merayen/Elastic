package net.merayen.elastic.ui.objects.components;

import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.node.UINode;

/**
 * To be used with InputSignalParametersProcessor that sits on the other end (backend) and actually transforms the signal.
 */
public class InputSignalParameters extends UIObject {
	public final PopupParameter1D amplitude = new PopupParameter1D();
	public final PopupParameter1D offset = new PopupParameter1D();

	private float amplitude_value;
	private float offset_value;

	public final String name;
	private final UINode node;

	/**
	 * @param node Uses this node to send messages to backend
	 * @param name Couples the InputSignalParametersProcessor() in the backend. They have to have the same name.
	 */
	public InputSignalParameters(UINode node, String name) {
		this.name = name;
		this.node = node;
	}

	@Override
	protected void onInit() {
		amplitude.translation.x = 0;
		amplitude.translation.y = 0;
		amplitude.label.text = "Amplitude";
		amplitude.drag_scale = 1f;
		add(amplitude);

		offset.translation.y = 0;
		offset.label.text = "Offset";
		offset.drag_scale = 1f;
		add(offset);

		amplitude.setHandler(new PopupParameter1D.Handler() {
			@Override
			public void onMove(float value) {
				amplitude_value = (float)Math.pow(value * 2, 14);
				updateTexts();
				sendParameters();
			}

			@Override
			public void onChange(float value) {}
		});

		offset.setHandler(new PopupParameter1D.Handler() {
			@Override
			public void onMove(float value) {
				offset_value = (float)(Math.pow(Math.max(0.5f, value) * 2, 14) - Math.pow(Math.max(0.5f, 1-value) * 2, 14));
				updateTexts();
				sendParameters();
			}

			@Override
			public void onChange(float value) {}
		});

		updateTexts();
	}

	@Override
	protected void onUpdate() {
		offset.translation.x = amplitude.label.getWidth() + 10;
	}

	public float getAmplitude() {
		return amplitude_value;
	}

	private void setAmplitude(float v) {
		amplitude.setValue((float)Math.pow(v, 1/14.0) / 2);
		amplitude_value = v;
	}

	public float getOffset() {
		return offset_value;
	}

	private void setOffset(float v) {
		float value = (float)(Math.pow(Math.max(1, v), 1/14.0) - Math.pow(Math.max(1, -v), 1/14.0)) / 2 + .5f;
		offset.setValue(value);
		offset_value = v;
	}

	@SuppressWarnings("unchecked")
	public void handleMessage(NodeParameterMessage message) {
		if(message.key.equals("data.InputSignalParameters:" + name)) {
			setAmplitude(((Number)((Map<String,Object>)message.value).get("amplitude")).floatValue());
			setOffset(((Number)((Map<String,Object>)message.value).get("offset")).floatValue());

			updateTexts();
		}
	}

	private void updateTexts() {
		amplitude.label.text = String.format("Amplitude: %.3f", getAmplitude());
		offset.label.text = String.format("Offset: %.3f", getOffset());
	}

	@SuppressWarnings("serial")
	private void sendParameters() {
		node.sendParameter("data.InputSignalParameters:" + name, new HashMap<String, Object>(){{
			put("amplitude", getAmplitude());
			put("offset", getOffset());
		}});
	}
}
