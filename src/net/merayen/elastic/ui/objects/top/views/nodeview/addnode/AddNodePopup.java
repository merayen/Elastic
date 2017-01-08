package net.merayen.elastic.ui.objects.top.views.nodeview.addnode;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.popupslide.PopupSlide;
import net.merayen.elastic.ui.objects.top.Top;

public class AddNodePopup {
	private final Top top;

	public AddNodePopup(UIObject object) {
		top = (Top)object.search.getTop();

		PopupSlide ps = new PopupSlide();

		top.overlay.add(ps);

		ps.openPopup(new AddNodePopupSlideItem());
	}
}
