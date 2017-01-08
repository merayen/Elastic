package net.merayen.elastic.ui.objects.top.views.nodeview.addnode;

import net.merayen.elastic.ui.objects.popupslide.PopupSlideItem;

class NodeListPopupSlideItem extends PopupSlideItem {
	private static class Title extends PopupSlideItem.Title {

	}

	private static class Content extends PopupSlideItem.Content {
		@Override
		protected void onInit() {}

		@Override
		protected void onDraw() {
			super.onDraw();

			draw.setColor(200, 200, 200);
			draw.setFont("", 12);
			draw.text("Will show nodes in category chosen", 20, 20);
		}
	}

	public NodeListPopupSlideItem(Title title, Content content) {
		super(new Title(), new Content());
	}
}
