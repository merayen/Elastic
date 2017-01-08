package net.merayen.elastic.ui.objects.components.list;

import net.merayen.elastic.ui.UIObject;

public class UIListItem extends UIObject {
	public float width = 100;
	public float height = 20;

	public boolean auto_size = true;

	@Override
	protected void onUpdate() {
		if(auto_size)
			; // TODO
	}
}
