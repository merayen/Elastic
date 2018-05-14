package net.merayen.elastic.ui.objects.top.megamenu;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Button;

public class MegaMenu extends UIObject {
	private Button button = new Button();
	private final Menu menu = new Menu();

	@Override
	public void onInit() {
		button.setLabel("Menu");
		button.setHandler( () -> {
			if(menu.getParent() == null)
				add(menu);
			else
				remove(menu);
		});
		add(button);
		menu.getTranslation().y = 20;
	}
}
