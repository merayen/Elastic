package net.merayen.merasynth.ui.objects.dialogs;

import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.components.Button;
import net.merayen.merasynth.ui.objects.window.Window;

public class AboutDialog extends UIGroup {
	private static class AboutDialogContent extends UIGroup {
		protected void onDraw() {
			draw.setColor(200, 200, 200);
			draw.setFont("Geneva", 2f);
			draw.text("MeraSynth v0.0.1", 10f, 5f);
		}
	}

	private Window window;

	protected void onInit() {
		AboutDialog self = this;
		window = new Window();
		window.translation.x = 20f;
		window.translation.y = 20f;
		window.width = 60f;
		window.height = 60f;
		window.whenReady( () -> {
			UIGroup pane = window.getContentPane();
			pane.add(new AboutDialogContent());

			Button button = new Button();
			button.label = "Close";
			button.translation.x = 50f;
			button.translation.y = 55f;
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
