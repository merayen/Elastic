package net.merayen.elastic.ui.objects.top.views.nodeview.addnode;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.popupslide.PopupSlide;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.uinodes.BaseInfo;

public class AddNodePopup {
	public interface Handler {
		public void onSelectNode(BaseInfo info);
	}

	private final Top top;
	private final PopupSlide popup;
	private final Handler handler;

	public AddNodePopup(UIObject object, Handler handler) {
		top = (Top)object.search.getTop();
		this.handler = handler;

		popup = new PopupSlide();

		top.overlay.add(popup);

		openAddNodePopup();
	}

	private void openAddNodePopup() {
		popup.openPopup(new AddNodePopupSlideItem(new AddNodePopupSlideItem.Handler() {
			@Override
			public void onSelectCategory(String category) {
				openNodeList(category);
			}
		}));
	}

	private void openNodeList(String category) {
		popup.openPopup(new NodeListPopupSlideItem(category, new NodeListPopupSlideItem.Handler() {
			@Override
			public void onSelect(BaseInfo info) {
				handler.onSelectNode(info);
				popup.closePopup();
			}
		}));
	}
}
