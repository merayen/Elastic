package net.merayen.elastic.ui.objects.top.views.nodeview.addnode;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu;
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem;
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem;
import net.merayen.elastic.ui.objects.popupslide.PopupSlideItem;

class AddNodePopupSlideItem extends PopupSlideItem {
	interface Handler {
		public void onAddNode();
	}

	private static class Title extends PopupSlideItem.Title {
		@Override
		protected void onDraw() {
			super.onDraw();

			drawText("Add node");
		}
	}

	private static class Content extends PopupSlideItem.Content {
		ContextMenu menu;

		final ContextMenuItem import_item = new TextContextMenuItem("Import");
		final ContextMenuItem generators_item = new TextContextMenuItem("Generators");
		final ContextMenuItem filters_item = new TextContextMenuItem("Filters");
		final ContextMenuItem output_item = new TextContextMenuItem("Output");
		final ContextMenuItem link_item = new TextContextMenuItem("Link");
		final ContextMenuItem library_item = new TextContextMenuItem("Library");

		@Override
		protected void onInit() {
			menu = new ContextMenu(this, 8, new ContextMenu.Handler() {
				@Override
				public void onSelect(ContextMenuItem item) { // TODO move stuff below out to a separate class
					System.out.println(((TextContextMenuItem)item).text);

					/*if(item == add_node_item) {
						new AddNodePopup(self);
					}*/
				}
			});

			menu.addMenuItem(import_item);
			menu.addMenuItem(generators_item);
			menu.addMenuItem(filters_item);
			menu.addMenuItem(output_item);
			menu.addMenuItem(library_item);
			menu.addMenuItem(link_item);
		}

		@Override
		protected void onDraw() {
			super.onDraw();

			draw.setColor(200, 200, 200);
			draw.setFont("", 12);
			draw.text("Will show search and categories", 20, 20);
		}

		@Override
		protected void onEvent(IEvent event) {
			menu.handle(event);
		}
	}

	public AddNodePopupSlideItem() {
		super(new Title(), new Content());
	}

	@Override
	protected void onInit() {
		super.onInit();

		width = 400;
		height = 500;
	}
}
