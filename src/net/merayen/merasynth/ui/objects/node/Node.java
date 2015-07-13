package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Titlebar;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class Node extends Group {
	public float width = 50f;
	public float height = 50f;

	protected Titlebar titlebar;
	protected ArrayList<Port> ports = new ArrayList<Port>();

	protected void onDump(JSONObject state) {}
	protected void onRestore(JSONObject state) {}

	protected void onInit() {
		titlebar = new Titlebar();
		add(titlebar);
	}

	@Override
	protected void onDraw() {
		draw.setColor(80, 80, 80);
		draw.fillRect(-0.1f, -0.1f, width + 0.2f, height + 0.2f);

		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);

		draw.setColor(180, 180, 180);
		draw.fillRect(0.1f, 0.1f, width - 0.2f, height - 0.2f);

		draw.setColor(100, 100, 100);
		draw.fillRect(0.2f, 0.2f, width - 0.4f, height - 0.4f);

		titlebar.width = width;

		super.onDraw();
	}

	protected void addPort(Port port) {
		add(port);
		ports.add(port);
	}

	public JSONObject dump() {
		JSONObject result = new JSONObject();
		JSONObject state = new JSONObject();

		onDump(state);

		result.put("state", state);
		result.put("id", getID());
		result.put("x", translation.x);
		result.put("y", translation.y);
		result.put("class", this.getClass().getName());

		return result;
	}

	public void restore(JSONObject obj) {
		onRestore((JSONObject)obj.get("state"));
		translation.x = (Float)obj.get("x");
		translation.y = (Float)obj.get("y");
	}
}
