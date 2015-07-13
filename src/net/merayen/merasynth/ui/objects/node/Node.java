package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Titlebar;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class Node extends Group {
	public float width = 50f;
	public float height = 50f;

	protected Titlebar titlebar;
	protected ArrayList<NodePort> ports = new ArrayList<NodePort>();

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

	public void addInputPort(Port port) {
		add(port);
		ports.add(new NodePort(port, false));
	}

	public void addOutputPort(Port port) {
		add(port);
		ports.add(new NodePort(port, true));
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
		assert obj.get("class").equals(this.getClass().getName());

		this.setID((String)obj.get("id")); 
		translation.x = ((Double)obj.get("x")).floatValue();
		translation.y = ((Double)obj.get("y")).floatValue();

		onRestore((JSONObject)obj.get("state"));
	}

	public static Node createFromClassPath(String class_path) {
		net.merayen.merasynth.ui.objects.node.Node uinode;

		try {
			uinode = ((Class<net.merayen.merasynth.ui.objects.node.Node>)Class.forName(class_path)).newInstance();
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create UINode");
		}

		return uinode;
	}
}
