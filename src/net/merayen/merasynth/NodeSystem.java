package net.merayen.merasynth;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.system.Restoration;
import net.merayen.merasynth.ui.DrawContext;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.event.MouseEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.surface.Surface;
import net.merayen.merasynth.ui.surface.Swing;

public class NodeSystem {
	/*
	 * This class is supposed to contain UINode, NetList and GlueNodes.
	 * This will also be the topmost class for the node system!
	 * TODO Give it another name, for Mr Christ's sake (Japanese sake, yes)
	 * Note that nodes that group other notes will contain another NodeSystem() and will
	 * handle all the in and out communication and events.
	 */

	// GlueNodes
	private net.merayen.merasynth.glue.Context glue_node_context;

	// NetList nodes
	

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
		glue_node_context = new net.merayen.merasynth.glue.Context();
	}

	public void initNetNodeSystem() {
		
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

				top_ui_object.update(dc);

				executeDelayEvents(dc.outgoing_events);

				// TODO Route outgoing events, if we are contained by container node
			}
		});

		top_ui_object.translation.scale_x = 100f; // TODO Update by aspect ratio of current window size
		top_ui_object.translation.scale_y = 100f;

		for(int i = 0; i < 1; i++) { // TODO Remove
			net.merayen.merasynth.ui.objects.node.Node node = new net.merayen.merasynth.ui.objects.client.PulseGenerator();
			node.translation.x = 0f + i;
			node.translation.y = 0f + i;
			node.width = 10f;
			node.height = 10f;
			top_ui_object.addNode(node);
		}
	}

	private void executeDelayEvents(ArrayList<net.merayen.merasynth.ui.event.IEvent>events) {
		for(net.merayen.merasynth.ui.event.IEvent e : events)
			if(e instanceof DelayEvent)
				((DelayEvent) e).run();
	}

	public void addNode(Class<? extends GlueNode> node) {
		// TODO
		// This adds a GlueNode, a Netlist node and a UI node (?)
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
}
