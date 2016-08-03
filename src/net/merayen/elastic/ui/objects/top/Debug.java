package net.merayen.elastic.ui.objects.top;

import java.util.Arrays;
import java.util.HashMap;

import net.merayen.elastic.ui.UIObject;

public class Debug extends UIObject {
	private static final double SCALE = 10; 
	HashMap<String,String> list = new HashMap<>();
	HashMap<String,Double> updated = new HashMap<>();

	public void set(String key, Object value) {
		list.put(key, value != null ? value.toString() : "null");
		updated.put(key, System.currentTimeMillis() / 1000.0);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		Object[] m = list.keySet().toArray();
		Arrays.sort(m);

		double t = System.currentTimeMillis() / 1000.0;
		int i = -1;
		for(Object k : m) {
			String x = (String)k;

			if(i > 50)
				break;

			double b = t - updated.get(x);
			if(b > SCALE) {
				unset(x);
				continue;
			}

			i++;

			float fac = 1 - (float)(Math.min(b, SCALE) / SCALE);
			if(fac < 0 || fac > 1)
				System.out.println("Nei");

			String text = String.format("%s: %s", x, list.get(x)); 
			draw.setFont("Arial", 1.3f);
			draw.setColor(100, 100, 100);
			draw.text(text, 0.2f, 0.1f + 1.2f * i);
			draw.setColor((int)(100 + 154 * fac), (int)(100 + 154 * fac), (int)(100 + 154 * fac));
			draw.text(text, 0f, 1.2f * i);
		}
	}

	public void unset(String key) {
		list.remove(key);
		updated.remove(key);
	}
}
