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
	
	public Rect draw_outline = new Rect(Float.MAX_VALUE, Float.MAX_VALUE, 0, 0); // Calculated size of the drawn area.
	public java.awt.Rectangle draw_outline_absolute = new java.awt.Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0); // Absolute in pixels!
	
	public Draw(UIObject obj, java.awt.Graphics2D g) {
		uiobject = obj;
		g2d = g;
	}
	
	public void setObject(UIObject obj) {
		this.uiobject = obj;
	}
	
	private void reg(
			float x, float y, float width, float height,
			int a_x, int a_y, int a_width, int a_height
	) {
		//if(uiobject instanceof net.merayen.merasynth.ui.objects.node.Titlebar)
		//	System.out.printf("%f, %f, %f, %f\n", x, y, width, height);
		draw_outline.x = Math.min(x, draw_outline.x);
		draw_outline.y = Math.min(y, draw_outline.y);
		draw_outline.width = Math.max(width, draw_outline.width);
		draw_outline.height = Math.max(height, draw_outline.height);
		
		draw_outline_absolute.x = Math.min(a_x, draw_outline_absolute.x);
		draw_outline_absolute.y = Math.min(a_y, draw_outline_absolute.y);
		draw_outline_absolute.width = Math.max(a_width, draw_outline_absolute.width);
		draw_outline_absolute.height = Math.max(a_height, draw_outline_absolute.height);
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
	
	public void text(String text, float x, float y) {
		java.awt.Point point = uiobject.getAbsolutePixelPoint(x, y);
		// TODO calculate the outline box, reg(...)
		g2d.drawString(text, point.x, point.y);
	}
}
