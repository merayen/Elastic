package net.merayen.elastic.ui.objects.dialogs;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Button;
import net.merayen.elastic.ui.objects.window.Window;

public class AboutDialog extends UIObject {
	private static class AboutDialogContent extends UIObject {
		protected void onDraw() {
			draw.setColor(200, 200, 200);
			draw.setFont("Geneva", 20f);
			draw.text("Elastic v0.0.1", 100f, 50f);
		}
	}

	private Window window;

	protected void onInit() {
		AboutDialog self = this;
		window = new Window();
		window.width = 600f;
		window.height = 600f;
		//window.center(getTopObject().getScreenWidth(), getTopObject().getScreenHeight());
		window.whenReady( () -> {
			UIObject pane = window.getContentPane();
			pane.add(new AboutDialogContent());

			Button button = new Button();
			button.label = "Close";
			button.translation.x = 500f;
			button.translation.y = 550f;
			button.setHandler(new Button.IHandler() {
				@Override
				public void onClick() {
					self.parent.remove(self);
				}
			});
			pane.add(button);
		});
		add(window);

		super.onInit();
	}
}
