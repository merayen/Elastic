package net.merayen.elastic.ui.objects.top;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.views.View;

/**
 * Used by the Top-node to contain all the views.
 * This uiobject itself is not visible (not drawn at all),
 * but Viewports can retrieve the different Views from us
 * and draw them on themself.
 * This UIObject is just a container.
 */
class ViewContainer extends UIObject {
	private List<View> views = new ArrayList<>();

	@Override
	protected void onInit() {
		super.onInit();
		translation.visible = false; // We are never drawn
	}

	public void addView(View view) {
		views.add(view);
		add(view);
	}
}
