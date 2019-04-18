package net.merayen.elastic.ui.objects.components.autolayout;

import java.util.List;

import net.merayen.elastic.ui.UIObject;

public class AutoLayout<T extends AutoLayout.Placement> extends UIObject {
	public interface Placement {
		void place(List<UIObject> objects);
		float getWidth();
		float getHeight();
	}

	public final T placement;

	public AutoLayout(T placement) {
		this.placement = placement;
	}

	@Override
	public void onUpdate() {
		placement.place(getSearch().getChildren());
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
