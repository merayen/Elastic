package net.merayen.merasynth.ui.objects.top;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.objects.top.menu.Bar;
import net.merayen.merasynth.ui.objects.top.menu.MenuBarItem;
import net.merayen.merasynth.ui.objects.top.menu.MenuListItem;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Top extends Group {
	/*
	 * The topmost object, of 'em all
	 */
	private MouseHandler mousehandler;

	// Scrolling, when dragging the background
	float start_scroll_x;
	float start_scroll_y;

	private TopNodeContainer top_node_container = new TopNodeContainer();
	private Bar menu = new Bar(); // The menu always displayed at top

	protected void onInit() {
		add(top_node_container);
		add(menu);

		menu.width = 100f; // Since our scale_x is 100, this one is 100
		fillMenuBar();

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				top_node_container.translation.scroll_x = (start_scroll_x - offset.x);
				top_node_container.translation.scroll_y = (start_scroll_y - offset.y);
			}

			@Override
			public void onMouseDown(Point position) {
				start_scroll_x = top_node_container.translation.scroll_x;
				start_scroll_y = top_node_container.translation.scroll_y;
			}
		});
	}

	protected void onDraw() {
		draw.setColor(50, 50, 50);
		draw.fillRect(-draw_context.width/2, -draw_context.height/2, draw_context.width, draw_context.height); // XXX Ikke bruk draw_context, men meh

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			float p_x = top_node_container.translation.scale_x;
			float p_y = top_node_container.translation.scale_y;

			if(e.getOffsetY() < 0) {
				top_node_container.translation.scale_x /= 1.1;
				top_node_container.translation.scale_y /= 1.1;
			}
			else if(e.getOffsetY() > 0) {
				top_node_container.translation.scale_x *= 1.1;
				top_node_container.translation.scale_y *= 1.1;
			} else {
				return;
			}

			top_node_container.translation.scale_x = Math.min(Math.max(top_node_container.translation.scale_x, .1f), 10f);
			top_node_container.translation.scale_y = Math.min(Math.max(top_node_container.translation.scale_y, .1f), 10f);

			top_node_container.translation.scroll_x -= (top_node_container.translation.scale_x - p_x)*translation.scale_x/2;
			top_node_container.translation.scroll_y -= (top_node_container.translation.scale_y - p_y)*translation.scale_y/2;
		}
	}

	public void addNode(Node node) {
		top_node_container.add(node);
	}

	private void fillMenuBar() {
		MenuBarItem file = new MenuBarItem();
		file.label = "File";
		menu.addMenuBarItem(file);

		MenuListItem new_project = new MenuListItem();
		new_project.label = "New project";
		file.menu_list.addMenuItem(new_project);

		MenuListItem quit = new MenuListItem();
		quit.label = "Quit";
		quit.setHandler(new MenuListItem.Handler() {
			@Override
			public void onClick() {
				System.out.println("Rage quitting");
			}
		});
		file.menu_list.addMenuItem(quit);


		MenuBarItem edit = new MenuBarItem();
		edit.label = "Edit";
		menu.addMenuBarItem(edit);

		MenuListItem undo = new MenuListItem();
		undo.label = "Undo";
		edit.menu_list.addMenuItem(undo);

		MenuBarItem hilfe = new MenuBarItem();
		hilfe.label = "Hilfe";
		menu.addMenuBarItem(hilfe);

		MenuListItem hilfeitem = new MenuListItem();
		hilfeitem.label = "Quit";
		hilfe.menu_list.addMenuItem(hilfeitem);

		Label logo = new Label();
		logo.label = "MeraSynth";
		logo.font_size = 1f;
		logo.translation.x = 1f;
		logo.translation.y = 98f;
		add(logo);
	}
}
