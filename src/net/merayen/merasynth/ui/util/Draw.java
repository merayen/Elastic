package net.merayen.merasynth.ui.util;

import java.awt.Font;

import net.merayen.merasynth.ui.Rect;
import net.merayen.merasynth.ui.objects.UIObject;

public class Draw {
	/*
	 * Helper class to make drawing easy inside the UIObject()s.
	 * UIObjects uses this.
	 * This class mostly translates our internal floating point coordinate system to pixels,
	 * making it easier to draw stuff, and abstracting away the underlaying painting system.
	 * TODO don't instantiate it on every uiobject and store the Z-index for all the drawings
	 */
	private java.awt.Graphics2D g2d;
	private UIObject uiobject;
	
	private class RectArea {
		public float
			x1 = Float.MAX_VALUE, y1 = Float.MAX_VALUE,
			x2 = Float.MIN_VALUE, y2 = Float.MIN_VALUE;
	}
	
	private RectArea outline = null;//new RectArea(, Float.MAX_VALUE, 0, 0); // Calculated size of the drawn area.
	private RectArea outline_abs = null;//new java.awt.Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0); // Calculated size of the drawn area, pixels absolute
	
	boolean skip_outline = false;
	
	// No idea what I thought with the below code?
	//public java.awt.Rectangle draw_outline_absolute = new java.awt.Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0); // Absolute in pixels!
	
	public Draw(UIObject obj, java.awt.Graphics2D g) {
		uiobject = obj;
		g2d = g;
	}
	
	public void setObject(UIObject obj) {
		this.uiobject = obj;
	}
	
	public Rect getRelativeOutline() {
		return outline == null ? null : new Rect(outline.x1, outline.y1, outline.x2 - outline.x1, outline.y2 - outline.y1);
	}
	
	public Rect getAbsoluteOutline() {
		return outline_abs == null ? null : new Rect(outline_abs.x1, outline_abs.y1, outline_abs.x2 - outline_abs.x1, outline_abs.y2 - outline_abs.y1);
	}
	
	private void reg(
			float x, float y, float width, float height,
			int a_x, int a_y, int a_width, int a_height
	) {
		if(skip_outline)
			return;

		if(outline == null) {
			outline = new RectArea();
			outline_abs = new RectArea();
		}

		outline.x1 = Math.min(x, outline.x1);
		outline.y1 = Math.min(y, outline.y1);
		outline.x2 = Math.max(width, outline.x2);
		outline.y2 = Math.max(height, outline.y2);

		outline_abs.x1 = Math.min(a_x, outline_abs.x1);
		outline_abs.y1 = Math.min(a_y, outline_abs.y1);
		outline_abs.x2 = Math.max(a_x + a_width, outline_abs.x2);
		outline_abs.y2 = Math.max(a_y + a_height, outline_abs.y2);
	}
	
	public void fillRect(float x, float y, float width, float height) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(
				x, y, width, height,
				point.x, point.y, dimension.width, dimension.height
		);
		g2d.fillRect(point.x, point.y, dimension.width, dimension.height);
	}
	
	public void setStroke(float width) {
		g2d.setStroke(new java.awt.BasicStroke(uiobject.convertUnitToPixel(width)));
	}
	
	public void setFont(String font_name, float size) {
		Font font = new Font(font_name, 0, (int)uiobject.convertUnitToPixel(size));
		g2d.setFont(font);
	}
	
	public void line(float x1, float y1, float x2, float y2) {
		java.awt.Point point1 = uiobject.getAbsolutePixelPoint(x1, y1);
		java.awt.Point point2 = uiobject.getAbsolutePixelPoint(x2, y2);
		reg(
				Math.min(x1, x2),
				Math.min(y1, y2),
				Math.abs(x2-x1),
				Math.abs(y2-y1),

				Math.min(point1.x, point2.x),
				Math.min(point1.y, point2.y),
				Math.abs(point2.x - point1.x),
				Math.abs(point2.y - point1.y)
		);
		g2d.drawLine(point1.x, point1.y, point2.x, point2.y);
	}
	
	public void fillOval(float x, float y, float width, float height) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(
				x, y, width, height,
				point.x, point.y, dimension.width, dimension.height
		);
		g2d.fillOval(point.x, point.y, dimension.width, dimension.height);
	}
	
	public void oval(float x, float y, float width, float height, float lineWidth) {
		// TODO implement lineWidth
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		reg(
				x, y, width, height,
				point.x, point.y, dimension.width, dimension.height
		);
		g2d.drawOval(point.x, point.y, dimension.width, dimension.height);
	}
	
	public void text(String text, float x, float y) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		// TODO calculate the outline box, reg(...)
		g2d.drawString(text, point.x, point.y);
	}
	
	public void empty(float x, float y, float width, float height) {
		/*
		 * Draw nothing, but increase the draw area. E.g to catch mouse clicks
		 * outside the drawn area.
		 */
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
		this.reg(
			x, y, width, height,
			point.x, point.y, dimension.width, dimension.height
		);
	}
	
	public void disableOutline() {
		skip_outline = true;
	}
	
	public void enableOutline() {
		skip_outline = false;
	}
}
