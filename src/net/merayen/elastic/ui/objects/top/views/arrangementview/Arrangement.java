package net.merayen.elastic.ui.objects.top.views.arrangementview;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Button;
import net.merayen.elastic.ui.objects.components.Scroll;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;

class Arrangement extends UIObject {
	float width, height;
	private TrackList track_list = new TrackList();
	private final Scroll arrangement_list_scroll = new Scroll(track_list);
	private AutoLayout button_bar = new AutoLayout(new LayoutMethods.HorizontalBox(5, 100000));
	private final List<UIObject> to_remove = new ArrayList<>();

	@Override
	protected void onInit() {
		Arrangement self = this;
		add(arrangement_list_scroll);
		add(button_bar);

		arrangement_list_scroll.translation.y = 20;

		button_bar.add(new Button() {{
			label = "New track";
			setHandler(() -> {
				Track track = new Track();
				track_list.add(track);
				track.setHandler(new Track.Handler() {
					@Override
					public void onRemove() {
						to_remove.add(track);
					}
				});
			});
		}});
	}

	@Override
	protected void onUpdate() {
		track_list.width = width;
		arrangement_list_scroll.width = width;
		arrangement_list_scroll.height = height - 20;

		for(UIObject obj : to_remove)
			track_list.remove(obj);
		to_remove.clear();
	}
}
