package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.objects.node.Titlebar;
import net.merayen.merasynth.ui.objects.top.Top;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public abstract class Node extends Group {
	protected class Action {

		/*
		 * Called everytime GlueNode creates a port.
		 * UINode needs to then create and draw the port afterwards.
		 */
	}
	public float width = 50f;
	public float height = 50f;

	protected Titlebar titlebar;
	protected ArrayList<NodePort> ports = new ArrayList<NodePort>();

	protected void onDump(JSONObject state) {}
	protected void onRestore(JSONObject state) {}

	private SoftReference<GlueNode> cache_glue_node = new SoftReference<GlueNode>(null);
	private boolean inited;

	public abstract void onCreatePort(String name);
	public abstract void onRemovePort(String name);

	protected void onInit() {
		titlebar = new Titlebar();
		add(titlebar);
		inited = true;
	}

	@Override
	protected void onDraw() {
		assert inited : "Forgotten super.onInit() ?";

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

	@Override
	public void add(UIObject obj) {
		if(obj instanceof Port)
			throw new RuntimeException("Add ports with addInputPort() or addOutputPort()");

		super.add(obj);
	}

	public void addInputPort(String name, Port port) {
		super.add(port);
		ports.add(new NodePort(name, port, false));
	}

	public void addOutputPort(String name, Port port) {
		super.add(port);
		ports.add(new NodePort(name, port, true));
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

	protected GlueNode getGlueNode() {
		GlueNode result = cache_glue_node.get();
		if(result == null) {
			result = getTopObject().getGlueNode(this);
			assert result != null : "Could not find node's GlueNode sibling";
			cache_glue_node = new SoftReference<GlueNode>(result);
		}
		return result;
	}

	private Top getTopObject() {
		Group x = this;
		while(x.parent != null)
			x = x.parent;

		if(!(x instanceof Top))
			throw new RuntimeException("Topmost uiobject is not Top()");

		return (Top)x;
	}

	public ArrayList<NodePort> getPorts() {
		return new ArrayList<NodePort>(ports);
	}

	public boolean hasPort(String name) {
		for(NodePort port : ports)
			if(port.name.equals(name))
				return true;

		return false;
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
