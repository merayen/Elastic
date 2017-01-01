package net.merayen.elastic.ui.objects.node;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.elastic.system.intercom.CreateNodePortMessage;
import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.RemoveNodePortMessage;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UINet;
import net.merayen.elastic.ui.objects.node.Titlebar;
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

	protected abstract void onCreatePort(UIPort port); // Node can customize the created UIPort in this function
	protected abstract void onRemovePort(UIPort port); // Node can clean up any resources belonging to the UIPort
	protected abstract void onMessage(NodeParameterMessage message);
	protected abstract void onData(NodeDataMessage message);

	public UINode() {
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

		super.onDraw();
	}

	@Override
	public void add(UIObject obj) {
		super.add(obj);
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

	@SuppressWarnings("unchecked")
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

	public void sendParameter(String key, Object value) {
		sendMessage(new NodeParameterMessage(node_id, key, value));
	}

	public void executeMessage(Postmaster.Message message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			if(m.key.equals("ui.java.translation.x") && !titlebar.isDragging())
				translation.x = (Float)m.value;
			else if(m.key.equals("ui.java.translation.y") && !titlebar.isDragging())
				translation.y = (Float)m.value;

			onMessage(m);

		} else if(message instanceof NodeDataMessage) {
			onData((NodeDataMessage)message);

		} else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;
			UIPort port = new UIPort(m.port, m.output);
			ports.add(port);
			add(port);
			onCreatePort(port);

		} else if(message instanceof RemoveNodePortMessage) {
			RemoveNodePortMessage m = (RemoveNodePortMessage)message;
			UIPort port = getPort(m.port);
			remove(port);
			ports.remove(port);
			onRemovePort(port);
		}
	}

	public UINet getUINet() { // TODO cache it
		UIObject c = this;
		while(!((c = c.getParent()) instanceof NodeView)); // Will crash hardstyle if not found

		return ((NodeView)c).getUINet();
	}
}
