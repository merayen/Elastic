package net.merayen.merasynth.ui.objects.top;

import java.util.Arrays;
import java.util.HashMap;

import net.merayen.merasynth.ui.objects.UIObject;

public class Debug extends UIObject {
	HashMap<String,String> list = new HashMap<>();

	public void set(String key, Object value) {
		list.put(key, value != null ? value.toString() : "null");
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		Object[] m = list.keySet().toArray();
		Arrays.sort(m);

		int i = -1;
		for(Object y : m) {
			String x = (String)y;
			i++;
			String text = String.format("%s: %s", x, list.get(x)); 
			draw.setFont("Arial", 1.3f);
			draw.setColor(100, 100, 100);
			draw.text(text, 0.2f, 10.1f + 1.2f * i);
			draw.setColor(255, 255, 255);
			draw.text(text, 0f, 10 + 1.2f * i);
		}
	}
}
