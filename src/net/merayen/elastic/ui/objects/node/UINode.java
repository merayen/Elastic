package net.merayen.elastic.ui.objects.node;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UINet;
import net.merayen.elastic.ui.objects.node.Titlebar;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView;
import net.merayen.elastic.util.Postmaster;

public abstract class UINode extends UIObject {
	public String node_id; // Same ID as in the backend-system, netlist etc
	public float width = 500f;
	public float height = 500f;

	protected Titlebar titlebar;
	protected ArrayList<UIPort> ports = new ArrayList<UIPort>();

	// Dumping and restoring of simple things like position and scaling. All other dumping should be done by GlueNode (and maybe netnode too)
	protected void onDump(JSONObject state) {}
	protected void onRestore(JSONObject state) {}

	private boolean inited;

	protected abstract void onCreatePort(String name);
	protected abstract void onRemovePort(String name);
	protected abstract void onMessage(NodeParameterMessage message);

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
		/*Stats s = getGlueNode().getStats();
		if(s != null) { // Statistics are only available for Net nodes that are AudioNodes
			for(UIPort p : this.getPorts()) // TODO Only show when enabled in the UI?
				if(s.ports.containsKey(p.name))
					p.setPortStats(s.ports.get(p.name));

			if(absolute_translation.scale_x < .5) {
				draw.setColor(255, 255, 255);
				draw.setFont("Verdana", 8);
				draw.text(String.format("%d",  s.processor_count), 0, -5);
			}
		}*/

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

	public boolean hasPort(String name) {
		for(UIPort port : ports)
			if(port.name.equals(name))
				return true;

		return false;
	}

	public static UINode createFromClassPath(String class_path) { // TODO remove?
		net.merayen.elastic.ui.objects.node.UINode uinode;

		try {
			uinode = ((Class<net.merayen.elastic.ui.objects.node.UINode>)Class.forName(class_path)).newInstance();
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create UINode");
		}

		return uinode;
	}

	protected void sendParameter(String key, Object value) {
		((Top)search.getTop()).sendMessageToBackend(new NodeParameterMessage(node_id, key, value));
	}

	public void executeMessage(Postmaster.Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			if(m.key.equals("ui.java.translation.x"))
				translation.x = (Float)m.value;
			else if(m.key.equals("ui.java.translation.y"))
				translation.y = (Float)m.value;
			else
				onMessage(m);
		}
	}

	protected UINet getUINet() {
		UIObject c = this;
		while(!((c = c.getParent()) instanceof NodeView)); // Will crash hardstyle if not found

		return ((NodeView)c).getUINet();
	}
}
