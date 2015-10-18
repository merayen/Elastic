package net.merayen.merasynth.client.vu;

import java.util.ArrayList;

import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.components.VUMeter;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;
import net.merayen.merasynth.ui.util.Draw;

public class UI extends UINode {
	private UIPort input_port;

	private Label status_label;
	private ArrayList<VUMeter> meters;

	// Information
	private int channels;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 50f;
		height = 50f;

		titlebar.title = "VU-meter";

		status_label = new Label();
		status_label.translation.y = 30f;
		add(status_label);
	}

	@Override
	protected void onDraw() {
		super.onDraw();
		float[] levels = ((Glue)getGlueNode()).getChannelLevels();

		if(meters == null || meters.size() != levels.length)
			initMeters(levels.length);

		setLevels(levels);

		status_label.translation.x = width / 2f - status_label.getLabelWidth() / 2f;
	}

	private void initMeters(int count) {
		if(meters == null)
			meters = new ArrayList<VUMeter>();

		for(VUMeter x : meters)
			remove(x);

		meters.clear();

		for(int i = 0; i < count; i++) {
			VUMeter vu = new VUMeter();
			meters.add(vu);
			add(vu);

			vu.translation.x = 20 + i * 100;
			vu.translation.y = 20;
			vu.width = 80;
			vu.height = 50;

			vu.setPanelDrawFunc(new VUMeter.PanelHandler() {
				@Override
				public void draw(Draw draw, float c_radius, double start_rad, double length_rad) {
					final int count = 20;
					for(int i = 0; i <= count; i++) {
						if(i > 3) {
							draw.setColor(120, 120, 120);
							draw.setStroke(0.5f);
						} else {
							draw.setColor(255, 50, 50);
							draw.setStroke(1f);
						}

						double n = start_rad + (i / (double)count) * length_rad;
						float x1 = (float)Math.sin(n) * c_radius;
						float y1 = (float)Math.cos(n) * c_radius;
						float x2 = (float)Math.sin(n) * c_radius * .9f;
						float y2 = (float)Math.cos(n) * c_radius * .9f;
						draw.line(x1, y1, x2, y2);
					}
				}
			});
		}

		width = meters.size() * 100 + 20;

		height = meters.size() > 0 ? 150 : 50;
		status_label.label = meters.size() > 0 ? "" : "No audio detected";
	}

	private void setLevels(float[] levels) {
		for(int i = 0; i < levels.length; i++)
			meters.get(i).setValue(levels[i]);
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 20f;
			addPort(input_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
