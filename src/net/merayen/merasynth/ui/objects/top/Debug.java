package net.merayen.merasynth.ui.objects.top;

import java.util.HashMap;

import net.merayen.merasynth.ui.objects.UIObject;

public class Debug extends UIObject {
	HashMap<String,String> list = new HashMap<>();

	public void set(String key, String value) {
		list.put(key, value);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		int i = -1;
		for(String x : list.keySet()) {
			i++;
			String text = String.format("%s: %s", x, list.get(x)); 
			draw.setFont("Arial", 1.2f);
			draw.setColor(100, 100, 100);
			draw.text(text, 0.2f, 10.1f + 1.2f * i);
			draw.setColor(255, 255, 255);
			draw.text(text, 0f, 10 + 1.1f * i);
		}
	}
}
