package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.node.UINode;
import org.jetbrains.annotations.NotNull;

/**
 * To be used with InputSignalParametersProcessor that sits on the other end (backend) and actually transforms the signal.
 */
public class InputSignalParameters extends UIObject {
	public interface Handler {
		void onChange(float amplitude, float offset);
	}

	public Handler handler;

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
	public void onInit() {
		amplitude.getTranslation().x = 0;
		amplitude.getTranslation().y = 0;
		amplitude.drag_scale = 1f;
		add(amplitude);

		offset.getTranslation().x = 100;
		offset.getTranslation().y = 0;
		offset.drag_scale = 1f;
		add(offset);

		amplitude.setHandler(new PopupParameter1D.Handler() {
			@Override
			public void onMove(float value) {
				amplitude_value = (float)Math.pow(value * 2, 14);
				sendParameters();
			}

			@Override
			public void onChange(float value) {}

			@NotNull
			@Override
			public String onLabel(float value) {
				return String.format("Amplitude: %.3f", getAmplitude());
			}
		});
		amplitude.setValue(0);

		offset.setHandler(new PopupParameter1D.Handler() {
			@Override
			public void onMove(float value) {
				offset_value = (float)(Math.pow(Math.max(0.5f, value) * 2, 14) - Math.pow(Math.max(0.5f, 1-value) * 2, 14));
				sendParameters();
			}

			@Override
			public void onChange(float value) {}

			@NotNull
			@Override
			public String onLabel(float value) {
				return String.format("Offset: %.3f", getOffset());
			}
		});
		offset.setValue(0);
	}

	public float getAmplitude() {
		return amplitude_value;
	}

	public void setAmplitude(float v) {
		amplitude.setValue((float)Math.pow(v, 1/14.0) / 2);
		amplitude_value = v;
	}

	public float getOffset() {
		return offset_value;
	}

	public void setOffset(float v) {
		float value = (float)(Math.pow(Math.max(1, v), 1/14.0) - Math.pow(Math.max(1, -v), 1/14.0)) / 2 + .5f;
		offset.setValue(value);
		offset_value = v;
	}

	private void sendParameters() {
		if (handler != null) {
			handler.onChange(getAmplitude(), getOffset());
		}
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
