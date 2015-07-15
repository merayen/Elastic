package net.merayen.merasynth;

import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.event.MouseEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.surface.Surface;
import net.merayen.merasynth.ui.surface.Swing;

/*
 * This class is supposed to contain UINode, NetList and GlueNodes.
 * This will also be the topmost class for the node system!
 * Note that nodes that group other notes will contain another NodeSystem() and will
 * handle all the in and out communication and events.
 */
public class NodeSystem {
	public static abstract class Handler {
		public void onClose() {}
	}

	private final int DUMP_VERSION = 1;

	// GlueNodes
	private net.merayen.merasynth.glue.Context glue_context;

	// UI Nodes
	ArrayList<net.merayen.merasynth.ui.event.IEvent> events_queue;
	private Surface surface;

	private Handler handler;
	private String current_project_path;
	private boolean inited = false;

	public NodeSystem() {
		init();
	}

	private void init() {
		initGlueNodeSystem();
		initSurface();
		initUINodeSystem();
	}

	private void reinit() {
		// Reinit from current nodesystem, using the same frame and panel
		initGlueNodeSystem();
		initUINodeSystem();
	}

	private void initGlueNodeSystem() {
		glue_context = new net.merayen.merasynth.glue.Context();
		inited = false;
		current_project_path = null;
	}

	private void initSurface() {
		surface = new Swing(new Swing.Handler() { // TODO Instantiate Fake() when not topmost nodesystem

			@Override
			public void onMouseWheelEvent(MouseWheelEvent mouse_wheel_event) {
				events_queue.add(mouse_wheel_event);
			}

			@Override
			public void onMouseEvent(MouseEvent mouse_event) {
				mouse_event.calcHit(glue_context.top_ui_object);
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

				glue_context.top_ui_object.updateDraw(dc);

				glue_context.top_ui_object.updateEvents(dc);

				executeDelayEvents(dc.outgoing_events);

				// TODO Route outgoing events, if we are contained by container node
			}
		});
	}

	private void initUINodeSystem() {
		events_queue = new ArrayList<net.merayen.merasynth.ui.event.IEvent>();

		glue_context.top_ui_object.translation.scale_x = 100f; // TODO Update by aspect ratio of current window size
		glue_context.top_ui_object.translation.scale_y = 100f;

		glue_context.top_ui_object.setHandler(new net.merayen.merasynth.ui.objects.top.Top.Handler() {
			@Override
			public void onOpenProject(String project_path) {
				System.out.println("About to open: " + project_path);

				FileReader fr;
				JSONObject dump_obj;
				try {
					fr = new FileReader(project_path);
					dump_obj = (JSONObject)new org.json.simple.parser.JSONParser().parse(fr);
				} catch(java.io.IOException | org.json.simple.parser.ParseException e) {
					e.printStackTrace();
					return;
				}

				reinit();
				inited = true; // Makes us dirty again (not able to restore)
				restore(dump_obj);
			}

			@Override
			public void onSaveProject() {
				saveProject();
			}

			@Override
			public void onClose() {
				end();
				if(handler != null)
					handler.onClose();
			}
		});
	}

	private void executeDelayEvents(ArrayList<net.merayen.merasynth.ui.event.IEvent>events) {
		for(net.merayen.merasynth.ui.event.IEvent e : events)
			if(e instanceof DelayEvent)
				((DelayEvent) e).run();
	}

	public GlueNode addNode(Class<? extends GlueNode> node) {
		/*
		 * Adds a node to the system. Automatically creates for net, ui and gluenode system.
		 */
		inited = true;
		GlueNode glue_node_instance;
		try {
			glue_node_instance = node.getConstructor(net.merayen.merasynth.glue.Context.class).newInstance(glue_context);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not load GlueNode"); // TODO Show error message in the UI instead
		}
		net.merayen.merasynth.netlist.Node net_node = createNetNode(glue_node_instance);

		glue_node_instance.setNetNode(net_node);
		glue_context.net_supervisor.addNode(net_node);

		glue_node_instance.setUINode(glue_context.top_ui_object.addNode(glue_node_instance.getUINodePath()));
		glue_context.glue_top.addObject(glue_node_instance);

		glue_node_instance.doInit();

		return glue_node_instance;
	}

	public JSONObject dump() {
		// TODO dump the whole system, all 3 node types
		JSONObject result = new JSONObject();
		result.put("version", Info.version);
		result.put("dump_version", DUMP_VERSION);
		result.put("netnodes", glue_context.net_supervisor.dump());
		result.put("gluenodes", glue_context.glue_top.dump());
		result.put("uinodes", glue_context.top_ui_object.dump());
		return result;
	}

	public void restore(JSONObject obj) {
		// TODO handle different dump versions
		assert !inited : "Dumps can only be restored when in a clean state";
		inited = true;
		glue_context.top_ui_object.restore((JSONObject)obj.get("uinodes"));
		glue_context.net_supervisor.restore((JSONObject)obj.get("netnodes"));
		glue_context.glue_top.restore((JSONObject)obj.get("gluenodes"));
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void end() {
		/*
		 * Stop this nodesystem.
		 */
		surface.end();
	}

	private net.merayen.merasynth.netlist.Node createNetNode(GlueNode node) { // TODO Move to netnode-system?
		net.merayen.merasynth.netlist.Node netnode;

		try {
			netnode =  ((Class<net.merayen.merasynth.netlist.Node>)Class
				.forName(node.getNetNodePath()))
				.getConstructor(net.merayen.merasynth.netlist.Supervisor.class)
				.newInstance(glue_context.net_supervisor);
		} catch (SecurityException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not create NetNode");
		}

		return netnode;
	}

	private void saveProject() {
		if(current_project_path == null) {
			java.awt.FileDialog fd = new java.awt.FileDialog((java.awt.Frame)null);
			fd.setMode(java.awt.FileDialog.SAVE);
			fd.setVisible(true);
			if(fd.getFile() == null)
				return;
			current_project_path = fd.getDirectory() + fd.getFile();
		}
		java.io.PrintWriter pw;
		try {
			pw = new java.io.PrintWriter(current_project_path, "UTF-8");
		} catch (java.io.FileNotFoundException | java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
			// TODO Notify user
			return;
		}
		pw.println(dump().toJSONString());
		pw.close();
	}
}
