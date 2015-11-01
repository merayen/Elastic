package net.merayen.merasynth.ui.util;

import java.awt.Font;
import java.awt.FontMetrics;

import net.merayen.merasynth.ui.DrawContext;
import net.merayen.merasynth.ui.Rect;
import net.merayen.merasynth.ui.TranslationData;
import net.merayen.merasynth.ui.objects.UIObject;

public class Draw {
	/*
	 * Helper class to make drawing easy inside the UIObject()s.
	 * UIObjects uses this.
	 * Instantiated for every draw of every UIObject (Hello Java GC, Work bitch)
	 * This class mostly translates our internal floating point coordinate system to pixels,
	 * making it easier to draw stuff, and abstracting away the underlaying painting system.
	 * TODO don't instantiate it on every uiobject and store the Z-index for all the drawings
	 * TODO Abstract this, so we can present these functions to other draw systems (e.g on Android)
	 */
	public java.awt.Graphics2D g2d; // TODO Make private
	private UIObject uiobject;

	private Rect outline = null; // Relative

	private String font_name = "Geneva";
	private float font_size = 1f;

	boolean skip_outline = false;

	FontMetrics font_metrics;

	public Draw(UIObject obj, DrawContext dc) {
		uiobject = obj;
		g2d = dc.graphics2d;

		if(uiobject.absolute_translation.clip != null)
			clip(uiobject.absolute_translation.clip);

	}

	// Only to be called by UIObject.java, and must be called when finished drawing an object!
	public void destroy() {
		unclip();
		uiobject = null;
		g2d = null;
	}

	public Rect getAbsoluteOutline() {
		if(outline == null)
			return new Rect();

		TranslationData td = uiobject.absolute_translation;

		Rect r = new Rect(outline);
		if(td.clip != null)
			r.clip(td.clip);

		r.x1 = (r.x1 / td.scale_x + td.x);
		r.y1 = (r.y1 / td.scale_y + td.y);
		r.x2 = (r.x2 / td.scale_x + td.x);
		r.y2 = (r.y2 / td.scale_y + td.y);

		return r;
	}

	private void reg(float x, float y, float width, float height) {
		if(skip_outline)
			return;

		if(outline == null)
			outline = new Rect(x, y, x + width, y + height);
		else
			outline.enlarge(x, y, x + width, y + height);

		// TODO Only do once, not for every drawing (put inside getOutline()-something)
		// As outline also defines the hitbox for mouse events, the clip will be the maximum hitbox rectangle 
		if(uiobject.absolute_translation.clip != null) {
			TranslationData td = uiobject.absolute_translation;//.getFlattened();
			Rect c = uiobject.absolute_translation.clip;

			//outline.clip(c.x1 - td.x, c.y1 - td.y, c.x2 - td.x, c.y2 - td.y);
			//if(uiobject instanceof UIClip)
				//System.out.printf("Org: %s\nNew: %s\nAbs: %s\n\n", outline, new Rect(c.x1 - td.x, c.y1 - td.y, c.x2 - td.x, c.y2 - td.y),td);
		}
	}

	public void setColor(int r, int g, int b) {
		g2d.setColor(new java.awt.Color(r, g, b));
	}

	public void fillRect(float x, float y, float width, float height) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(x, y, width, height);
		g2d.fillRect(point.x, point.y, dimension.width, dimension.height);
	}

	public void rect(float x, float y, float width, float height) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(x, y, width, height);
		g2d.drawRect(point.x, point.y, dimension.width, dimension.height);
	}

	public void setStroke(float width) {
		g2d.setStroke(new java.awt.BasicStroke(uiobject.convertUnitToPixel(width)));
	}

	public void setFont(String font_name, float font_size) {
		if(font_name != null && font_name.length() > 0)
			this.font_name = font_name;
		this.font_size = font_size;
	}

	private void setFont() {
		Font font = new Font(font_name, 0, (int)uiobject.convertUnitToPixel(font_size));
		g2d.setFont(font);
	}

	public void line(float x1, float y1, float x2, float y2) {
		java.awt.Point point1 = uiobject.getAbsolutePixelPoint(x1, y1);
		java.awt.Point point2 = uiobject.getAbsolutePixelPoint(x2, y2);
		g2d.drawLine(point1.x, point1.y, point2.x, point2.y);
	}

	public void fillOval(float x, float y, float width, float height) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(x, y, width, height);
		g2d.fillOval(point.x, point.y, dimension.width, dimension.height);
	}

	public void oval(float x, float y, float width, float height, float lineWidth) {
		// TODO implement lineWidth
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(x, y, width, height);
		g2d.drawOval(point.x, point.y, dimension.width, dimension.height);
	}

	public void text(String text, float x, float y) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		// TODO calculate the outline box, reg(...)
		setFont();
		g2d.drawString(text, point.x, point.y);
	}

	public float getTextWidth(String text) {
		setFont();
		return uiobject.convertPixelToUnit(g2d.getFontMetrics().stringWidth(text));
	}

	public void empty(float x, float y, float width, float height) {
		/*
		 * Draw nothing, but increase the draw area. E.g to catch mouse clicks
		 * outside the drawn area.
		 */
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(x, y, width, height);
	}

	/*
	 * Only draw inside this rectangle.
	 * Absolute, in our internal coordinates (X and Y == 0 to 1)
	 */
	private void clip(Rect rect) {
		/*java.awt.Rectangle r = new java.awt.Rectangle(
			(int)(rect.x1 * draw_context.width),
			(int)(rect.y1 * draw_context.height),
			(int)((rect.x2 - rect.x1) * draw_context.width),
			(int)((rect.y2 - rect.y1) * draw_context.height)
		);*/

		java.awt.Rectangle r = new java.awt.Rectangle(
			(int)(rect.x1),
			(int)(rect.y1),
			(int)((rect.x2 - rect.x1)),
			(int)((rect.y2 - rect.y1))
		);

		/*String v = String.format("Draw.java Clip rect [%s] ", uiobject.getID());
		uiobject.getTopObject().debug.set(v + "rect", rect.toString());
		uiobject.getTopObject().debug.set(v + "r", r.toString());*/

		g2d.clip(r);
	}

	private void unclip() {
		g2d.setClip(null);
	}

	public void disableOutline() {
		skip_outline = true;
	}

	public void enableOutline() {
		skip_outline = false;
	}

	/*
	 * Shows debug for the current UIObject
	 */
	public void debug() {
		boolean prev = skip_outline;
		skip_outline = true;
		if(outline != null) {
			setColor(255, 255, 0);
			this.setStroke(0.5f);
			rect(outline.x1, outline.y1, outline.x2 - outline.x1, outline.y2 - outline.y1);
		}

		if(uiobject.translation.clip != null) {
			Rect c = uiobject.translation.clip;
			setColor(0, 0, 255);
			rect(c.x1, c.y1, c.x2 - c.x1, c.y2 - c.y1);
		}

		skip_outline = prev;
	}
}
