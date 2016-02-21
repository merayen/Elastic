package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.netlist.util.Stats;
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
	public float width = 500f;
	public float height = 500f;

	protected Titlebar titlebar;
	protected ArrayList<UIPort> ports = new ArrayList<UIPort>();

	// Dumping and restoring of simple things like position and scaling. All other dumping should be done by GlueNode (and maybe netnode too)
	protected void onDump(JSONObject state) {}
	protected void onRestore(JSONObject state) {}

	private SoftReference<GlueNode> cache_glue_node = new SoftReference<GlueNode>(null);
	private boolean inited;

	protected abstract void onCreatePort(String name);
	protected abstract void onRemovePort(String name);

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
		draw.fillRect(-1f, -1f, width + 2f, height + 2f);

		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);

		draw.setColor(180, 180, 180);
		draw.fillRect(1f, 1f, width - 2f, height - 2f);

		draw.setColor(100, 100, 100);
		draw.fillRect(2f, 2f, width - 4f, height - 4f);

		titlebar.width = width;

		// Doodling
		Stats s = getGlueNode().getStats();
		if(s != null) { // Statistics are only available for Net nodes that are AudioNodes
			for(UIPort p : this.getPorts()) // TODO Only show when enabled in the UI?
				if(s.ports.containsKey(p.name))
					p.setPortStats(s.ports.get(p.name));

			if(absolute_translation.scale_x < .5) {
				draw.setColor(255, 255, 255);
				draw.setFont("Verdana", 8);
				draw.text(String.format("%d",  s.processor_count), 0, -5);
			}
		}

		super.onDraw();
	}

	@Override
	public void add(UIObject obj) {
		if(obj instanceof UIPort)
			throw new RuntimeException("Add ports with addPort()");

		super.add(obj);
	}

	/**
	 * The class inheriting UINode can call this to add a new port
	 */
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

	public void removePort(UIPort port) {
		super.remove(port);
		ports.remove(port);
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
		result.put("scale_x", translation.scale_x);
		result.put("scale_y", translation.scale_y);
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

	/**
	 * Don't call this manually.
	 */
	public void gluenode_createPort(String port_name) {
		onCreatePort(port_name);

		// Reload UINet from netlist, in case this port is connected (typical after a restore, where connections are made previously in netlist)
		getTopObject().getUINet().reload();
	}

	/**
	 * Don't call this manually.
	 */
	public void gluenode_removePort(String port_name) {
		onRemovePort(port_name);

		// Reload UINet from netlist, in case this port is connected (typical after a restore, where connections are made previously in netlist)
		getTopObject().getUINet().reload();
	}
}
