package net.merayen.elastic.ui.objects.components.autolayout;

import java.util.List;

import net.merayen.elastic.ui.UIObject;

public class AutoLayout extends UIObject {
	public interface Placement {
		public void place(List<UIObject> current);
		public float getWidth();
		public float getHeight();
	}

	public final Placement placement;

	public AutoLayout(Placement placement) {
		this.placement = placement;
	}

	@Override
	public void add(UIObject element, int index) {
		super.add(element, index);
	}

	@Override
	protected void onUpdate() {
		placement.place(search.getChildren());
	}

	@Override
	public float getWidth() {
		return placement.getWidth();
	}

	@Override
	public float getHeight() {
		return placement.getHeight();
	}
}
