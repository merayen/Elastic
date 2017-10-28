package net.merayen.elastic.ui.objects.top.views.arrangementview;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;

class TrackList extends AutoLayout {
	public float width;

	public TrackList() {
		super(new LayoutMethods.HorizontalBox());
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		for(UIObject obj : search.getChildren()) {
			Track track = ((Track)obj);
			track.width = width - 10;
		}
	}
}
