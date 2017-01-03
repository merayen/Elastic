package net.merayen.elastic.ui.objects.contextmenu;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

/**
 * Puts up a context menu on top of everything (Top().overlay)
 */
public class ContextMenu {
	public interface Handler {
		public void onSelect(ContextMenuItem item);
	}

	private final Menu menu;
	private final MouseHandler mouse;

	public ContextMenu(UIObject trigger, Handler handler) {
		this(trigger, 8, handler);
	}

	public ContextMenu(UIObject trigger, int count, Handler handler) {
		menu = new Menu(count);

		mouse = new MouseHandler(trigger, MouseEvent.Button.RIGHT);
		mouse.setHandler(new MouseHandler.Handler() {
			float start_x, start_y;

			@Override
			public void onMouseDown(Point position) {
				((Top)trigger.search.getTop()).overlay.add(menu);
				Point absolute = trigger.getAbsolutePosition(position.x, position.y);
				start_x = absolute.x;
				start_y = absolute.y;
				menu.translation.x = absolute.x - menu.radius;
				menu.translation.y = absolute.y - menu.radius;

				menu.setPointer(0, 0);
			}

			@Override
			public void onMouseDrag(Point position, Point offset) {
				Point absolute = trigger.getAbsolutePosition(position.x, position.y);
				menu.setPointer(absolute.x - start_x, absolute.y - start_y);
			}

			@Override
			public void onGlobalMouseUp(Point position) {
				if(menu.getParent() != null) {
					((Top)trigger.search.getTop()).overlay.remove(menu);
					ContextMenuItem selected = menu.getSelected();
					if(selected != null)
						handler.onSelect(selected);
				}
			}
		});
	}

	public void handle(IEvent event) {
		mouse.handle(event);
	}

	public void addMenuItem(ContextMenuItem item) {
		menu.addMenuItem(item);
	}
}
