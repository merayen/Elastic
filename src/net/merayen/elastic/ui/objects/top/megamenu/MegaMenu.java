package net.merayen.elastic.ui.objects.top.megamenu;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Button;

public class MegaMenu extends UIObject {
	private Button button = new Button();
	private final Menu menu = new Menu();

	@Override
	protected void onInit() {
		button.label = "Menu";
		button.setHandler(() -> {
			if(menu.getParent() == null)
				add(menu);
			else
				remove(menu);
		});
		add(button);
		menu.translation.y = 20;
	}
}
