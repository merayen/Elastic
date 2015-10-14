package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.objects.node.Titlebar;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public abstract class UINode extends UIGroup {
	protected class Action {

		/*
		 * Called everytime GlueNode creates a port.
		 * UINode needs to then create and draw the port afterwards.
		 */
	}
	public float width = 50f;
	public float height = 50f;

	protected Titlebar titlebar;
	protected ArrayList<UIPort> ports = new ArrayList<UIPort>();

	// Dumping and restoring of simple things like position and scaling. All other dumping should be done by GlueNode (and maybe netnode too)
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
		if(!inited)
			throw new RuntimeException("Forgotten super.onInit() ?");

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
		if(obj instanceof UIPort)
			throw new RuntimeException("Add ports with addPort()");

		super.add(obj);
	}

	public void addPort(UIPort port) {
		super.add(port);
		ports.add(port);
		port.node_setHandler(new UIPort.Handler() {
			@Override
			public boolean onConnect(UIPort port) {
				// TODO Notify the GlueNode?
				return true;
			}

			@Override
			public void onDisconnect() {
				// TODO Notify the GlueNode?
			}
		});
	}

	public ArrayList<UIPort> getPorts() {
		return new ArrayList<UIPort>(ports);
	}

	public UIPort getPort(String name) {
		for(UIPort x : ports)
			if(x.name.equals(name))
				return x;

		return null;
	}

	public JSONObject dump() {
		JSONObject result = new JSONObject();
		JSONObject state = new JSONObject();

		onDump(state);

		result.put("state", state);
		result.put("id", getID());
		result.put("x", translation.x);
		result.put("y", translation.y);
		result.put("x", translation.scale_x);
		result.put("y", translation.scale_y);
		result.put("class", this.getClass().getName());

		return result;
	}

	public void restore(JSONObject obj) {
		if(!obj.get("class").equals(this.getClass().getName()))
			throw new RuntimeException(String.format("Can not restore from class. %s != %s", obj.get("class"), this.getClass().getName()));

		this.setID((String)obj.get("id")); 
		translation.x = ((Double)obj.get("x")).floatValue();
		translation.y = ((Double)obj.get("y")).floatValue(); // TODO Restore scaling
		translation.scale_x = ((Double)obj.get("scale_x")).floatValue();
		translation.scale_y = ((Double)obj.get("scale_y")).floatValue();

		onRestore((JSONObject)obj.get("state"));
	}

	public GlueNode getGlueNode() {
		GlueNode result = cache_glue_node.get();
		if(result == null) {
			result = getTopObject().getGlueNode(this);
			if(result == null)
				throw new RuntimeException("Could not find UI-node's GlueNode sibling");
			cache_glue_node = new SoftReference<GlueNode>(result);
		}
		return result;
	}

	public boolean hasPort(String name) {
		for(UIPort port : ports)
			if(port.name.equals(name))
				return true;

		return false;
	}

	public static UINode createFromClassPath(String class_path) {
		net.merayen.merasynth.ui.objects.node.UINode uinode;

		try {
			uinode = ((Class<net.merayen.merasynth.ui.objects.node.UINode>)Class.forName(class_path)).newInstance();
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create UINode");
		}

		return uinode;
	}
}
