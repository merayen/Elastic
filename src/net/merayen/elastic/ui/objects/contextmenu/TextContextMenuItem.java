package net.merayen.elastic.ui.objects.contextmenu;

/**
 * Only shows a label.
 */
public class TextContextMenuItem extends ContextMenuItem {
	public String text;

	public TextContextMenuItem(String text) {
		this.text = text;
	}

	public TextContextMenuItem() {}

	@Override
	protected void onDraw() {
		super.onDraw();

		float radius = getRadius();

		draw.setFont("", 10);
		float width = draw.getTextWidth(text);
		float font_size = (radius * 2f) / (width / 8);
		draw.setFont("", font_size);
		width = draw.getTextWidth(text);

		draw.setColor(255, 255, 255);

		draw.text(text, -width / 2 + getRadius(), getRadius() + font_size / 2.5f);
	}
}
