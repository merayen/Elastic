package net.merayen.elastic.ui.objects.components;

import java.util.HashMap;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.node.UINode;

/**
 * To be used with InputSignalParametersProcessor that sits on the other end (backend) and actually transforms the signal.
 */
public class InputSignalParameters extends UIObject {
	public final PopupParameter1D amplitude = new PopupParameter1D();
	public final PopupParameter1D offset = new PopupParameter1D();

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
				updateTexts();
				sendParameters();
			}

			@Override
			public void onChange(float value) {}
		});

		offset.setHandler(new PopupParameter1D.Handler() {
			@Override
			public void onMove(float value) {
				updateTexts();
				sendParameters();
			}

			@Override
			public void onChange(float value) {}
		});

		amplitude.setValue(0.5f);
		offset.setValue(0.5f);

		updateTexts();
	}

	@Override
	protected void onUpdate() {
		offset.translation.x = amplitude.label.getWidth() + 10;
	}

	public float getAmplitude() {
		return (float)Math.pow(amplitude.getValue() * 2, 14);
	}

	public float getOffset() {
		float v = offset.getValue();
		return (float)(Math.pow(Math.max(0.5f, v) * 2, 14) + -Math.pow(Math.max(0.5f, 1-v) * 2, 14));
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
