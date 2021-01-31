package net.merayen.elastic.ui.objects.top.views.nodeview.addnode;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.popupslide.PopupSlide;
import net.merayen.elastic.ui.objects.top.window.Window;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.uinodes.BaseInfo;

@Deprecated
public class AddNodePopup {
	public interface Handler {
		void onSelectNode(BaseInfo info);
	}

	private final Window window;
	private final PopupSlide popup;
	private final Handler handler;

	public AddNodePopup(UIObject uiobject, Handler handler) {
		window = UINodeUtil.INSTANCE.getWindow(uiobject);

		this.handler = handler;

		popup = new PopupSlide();

		window.getOverlay().add(popup);

		openAddNodePopup();
	}

	private void openAddNodePopup() {
		popup.openPopup(new AddNodePopupSlideItem(this::openNodeList));
	}

	private void openNodeList(String category) {
		popup.openPopup(new NodeListPopupSlideItem(category, info -> {
			handler.onSelectNode(info);
			popup.closePopup();
		}));
	}
}
