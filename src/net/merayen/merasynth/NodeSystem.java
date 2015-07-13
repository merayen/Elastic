package net.merayen.merasynth;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.system.Restoration;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.event.MouseEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.surface.Surface;
import net.merayen.merasynth.ui.surface.Swing;

public class NodeSystem {
	/*
	 * This class is supposed to contain UINode, NetList and GlueNodes.
	 * This will also be the topmost class for the node system!
	 * Note that nodes that group other notes will contain another NodeSystem() and will
	 * handle all the in and out communication and events.
	 */

	// GlueNodes
	private net.merayen.merasynth.glue.Context glue_context;
	private net.merayen.merasynth.glue.nodes.Top glue_top;

	// NetList nodes
	Supervisor net_supervisor;

	// UI Nodes
	ArrayList<net.merayen.merasynth.ui.event.IEvent> events_queue = new ArrayList<net.merayen.merasynth.ui.event.IEvent>();
	private net.merayen.merasynth.ui.objects.top.Top top_ui_object = new net.merayen.merasynth.ui.objects.top.Top(); // Topmost object containing everything
	private Surface surface;

	public NodeSystem() {
		initGlueNodeSystem();
		initNetNodeSystem();
		initUINodeSystem();
	}

	public void initGlueNodeSystem() {
		glue_context = new net.merayen.merasynth.glue.Context();
		glue_top = new net.merayen.merasynth.glue.nodes.Top(glue_context);
	}

	public void initNetNodeSystem() {
		net_supervisor = new Supervisor();
	}

	public void initUINodeSystem() {
		surface = new Swing(new Swing.Handler() { // TODO Instantiate Fake() when not topmost nodesystem

			@Override
			public void onMouseWheelEvent(MouseWheelEvent mouse_wheel_event) {
				events_queue.add(mouse_wheel_event);
			}

			@Override
			public void onMouseEvent(MouseEvent mouse_event) {
				mouse_event.calcHit(top_ui_object);
				events_queue.add(mouse_event);
			}

			@Override
			public void onDraw(java.awt.Graphics2D graphics2d) {
				ArrayList<net.merayen.merasynth.ui.event.IEvent> current_events;

				synchronized (events_queue) {
					current_events = new ArrayList<net.merayen.merasynth.ui.event.IEvent>(events_queue);
					events_queue.clear();
				}

				net.merayen.merasynth.ui.DrawContext dc = new net.merayen.merasynth.ui.DrawContext(graphics2d, current_events, surface.getWidth(), surface.getHeight());				

				top_ui_object.updateDraw(dc);

				top_ui_object.updateEvents(dc);

				executeDelayEvents(dc.outgoing_events);

				// TODO Route outgoing events, if we are contained by container node
			}
		});

		top_ui_object.translation.scale_x = 100f; // TODO Update by aspect ratio of current window size
		top_ui_object.translation.scale_y = 100f;
	}

	private void executeDelayEvents(ArrayList<net.merayen.merasynth.ui.event.IEvent>events) {
		for(net.merayen.merasynth.ui.event.IEvent e : events)
			if(e instanceof DelayEvent)
				((DelayEvent) e).run();
	}

	public void addNode(Class<? extends GlueNode> node) {
		GlueNode glue_node_instance;
		try {
			glue_node_instance = node.getConstructor(net.merayen.merasynth.glue.Context.class).newInstance(glue_context);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load GlueNode"); // TODO Show error message in the UI instead
		}
		net.merayen.merasynth.ui.objects.node.Node ui_node = createUINode(glue_node_instance);
		net.merayen.merasynth.netlist.Node net_node = createNetNode(glue_node_instance);

		glue_node_instance.setUINode(ui_node);
		glue_node_instance.setNetNode(net_node);

		top_ui_object.addNode(ui_node);
		glue_top.addObject(glue_node_instance);
	}

	public void restore(JSONObject dump) {
		// TODO restore from dump (and initialize all nodes of all 3 systems)
		// Probably create new class for restoring
		new Restoration(this, dump);
	}

	public JSONObject dump() {
		// TODO dump the whole system, all 3 node types
		return null;
	}

	private net.merayen.merasynth.ui.objects.node.Node createUINode(GlueNode node) {
		net.merayen.merasynth.ui.objects.node.Node uinode;

		try {
			uinode = ((Class<net.merayen.merasynth.ui.objects.node.Node>)Class.forName(node.getUINodePath())).newInstance();
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create UINode");
		}

		return uinode;
	}

	private net.merayen.merasynth.netlist.Node createNetNode(GlueNode node) {
		net.merayen.merasynth.netlist.Node netnode;

		try {
			netnode =  ((Class<net.merayen.merasynth.netlist.Node>)Class
				.forName(node.getNetNodePath()))
				.getConstructor(net.merayen.merasynth.netlist.Supervisor.class)
				.newInstance(net_supervisor);
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create NetNode");
		}

		return netnode;
	}
}
