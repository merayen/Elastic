package net.merayen.elastic.ui.objects.top.views;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.objects.top.menu.Bar;
import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.util.TaskExecutor;

public abstract class View extends UIObject {
	public float width = 100;
	public float height = 100;
	private boolean focused; // true when mouse is hovering over us, meaning we have the focus
	private Bar bar = new Bar(); // Shown in all viewports

	public abstract View cloneView();

	@Override
	protected void onInit() {
		super.onInit();
		add(bar);
	}

	@Override
	protected void onDraw() {
		if(focused) {
			draw.setColor(200, 200, 200);
			draw.setStroke(4);
			draw.rect(1, 1, width - 2, height - 2);
		}

		bar.translation.y = height - 20;
		bar.width = width;
	}

	@Override
	protected void onEvent(IEvent event) {
		if(event instanceof MouseEvent) {
			MouseEvent e = (MouseEvent)event;
			focused = (e.hitDepth(this) > -1);
		}
	}

	/**
	 * See if mouse is on this view.
	 */
	public boolean isFocused() {
		return focused;
	}

	/**
	 * Adds a task in to the closest ViewportContainer() domain.
	 */
	protected void addTask(TaskExecutor.Task task) {
		getViewport().getViewportContainer().addTask(task);
	}

	private Viewport getViewport() {
		UIObject c = this;
		while((c = c.getParent()) != null)
			if(c instanceof Viewport)
				return (Viewport)c;

		throw new RuntimeException("Viewport not found");
	}
}
