package net.merayen.elastic.ui.objects.top.views.nodeview.addnode;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods;
import net.merayen.elastic.ui.objects.popupslide.PopupSlideItem;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.uinodes.UINodeInformation;
import net.merayen.elastic.util.Point;

class AddNodePopupSlideItem extends PopupSlideItem {
	interface Handler {
		public void onSelectCategory(String category);
	}

	private static class Content extends PopupSlideItem.Content {
		interface Handler {
			public void onSelect(String text);
		}

		private final AutoLayout list = new AutoLayout(new LayoutMethods.HorizontalBox(10, 390));
		Handler handler;

		private void setHandler(Handler handler) {
			this.handler = handler;
		}

		@Override
		protected void onInit() {
			addCategory("Import node");

			for(String category : UINodeInformation.getCategories())
				addCategory(category);

			add(list);
		}

		private void addCategory(String text) {
			list.add(new UIObject() {
				MouseHandler mouse = new MouseHandler(this);
				boolean over;

				@Override
				protected void onInit() {
					mouse.setHandler(new MouseHandler.Handler() {
						@Override
						public void onMouseOver() {
							over = true;
						}

						@Override
						public void onMouseOut() {
							over = false;
						}

						@Override
						public void onMouseClick(Point position) {
							handler.onSelect(text);
						}
					});
				}

				@Override
				protected void onDraw() {
					float width = 100;
					float height = 100;

					if(over)
						draw.setColor(150, 200, 150);
					else
						draw.setColor(150, 150, 150);
					draw.fillRect(0, 0, width, height);

					draw.setColor(50, 50, 50);
					draw.setStroke(2);
					draw.rect(0, 0, width, height);

					draw.setColor(0, 0, 0);
					draw.setFont("", 16);
					draw.text(text, width / 2 - draw.getTextWidth(text) / 2, 20);
				}

				@Override
				protected void onEvent(IEvent event) {
					mouse.handle(event);
				}
			});
		}

		@Override
		protected void onDraw() {
			super.onDraw();

			draw.setColor(200, 200, 200);
			draw.setFont("", 12);
			draw.text("Will show search and categories", 20, 20);
		}
	}

	public AddNodePopupSlideItem(Handler handler) {
		super(new Content());

		((Content)content).setHandler(new Content.Handler() {
			@Override
			public void onSelect(String text) {
				title.text = "Category: " + text;
				handler.onSelectCategory(text);
			}
		});
	}

	@Override
	protected void onInit() {
		super.onInit();

		width = 408;
		height = 500;

		title.text = "Choose node category";
	}
}
