package net.merayen.elastic.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Path2D;

import net.merayen.elastic.ui.util.DrawContext;
import net.merayen.elastic.util.Point;

/**
 * Helper class to make drawing easy inside the UIObject()s.
 * UIObjects uses this.
 * Instantiated for every draw of every UIObject (Hello Java GC, Work bitch)
 * This class mostly translates our internal floating point coordinate system to pixels,
 * making it easier to draw stuff, and abstracting away the underlaying painting system.
 * TODO don't instantiate it on every uiobject and store the Z-index for all the drawings
 * TODO Abstract this, so we can present these functions to other draw systems (e.g on Android)
 */
public class Draw {
	private java.awt.Graphics2D g2d; // TODO Make private
	private UIObject uiobject;
	private DrawContext draw_context;

	Rect outline; // Relative

	private String font_name = "Geneva";
	private float font_size = 1f;

	private boolean skip_outline = false;

	FontMetrics font_metrics;

	Draw(UIObject obj, DrawContext dc) {
		uiobject = obj;
		draw_context = dc;
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

	public int getScreenWidth() {
		return draw_context.width;
	}

	public int getScreenHeight() {
		return draw_context.height;
	}

	public String getSurfaceID() {
		return draw_context.getSurfaceID();
	}

	public Rect getAbsoluteOutline() {
		TranslationData td = uiobject.absolute_translation;
		Rect r = (outline == null ? new Rect() : new Rect(outline));

		r.x1 = (r.x1 / td.scale_x + td.x);
		r.y1 = (r.y1 / td.scale_y + td.y);
		r.x2 = (r.x2 / td.scale_x + td.x);
		r.y2 = (r.y2 / td.scale_y + td.y);

		if(td.clip != null)
			r.clip(td.clip);

		return r;
	}

	private void reg(float x, float y, float width, float height) {
		if(skip_outline)
			return;

		if(outline == null)
			outline = new Rect(x, y, x + width, y + height);
		else
			outline.enlarge(x, y, x + width, y + height);
	}

	public void setColor(int r, int g, int b) {
		g2d.setColor(new java.awt.Color(r, g, b));
	}

	public void fillRect(float x, float y, float width, float height) {
		Point point = uiobject.getAbsolutePosition(x, y);
		Dimension dimension = uiobject.getAbsoluteDimension(width, height);
		reg(x, y, width, height);
		g2d.fillRect((int)point.x, (int)point.y, (int)dimension.width, (int)dimension.height);
	}

	public void rect(float x, float y, float width, float height) {
		Point point = uiobject.getAbsolutePosition(x, y);
		Dimension dimension = uiobject.getAbsoluteDimension(width, height);
		reg(x, y, width, height);
		g2d.drawRect((int)point.x, (int)point.y, (int)dimension.width, (int)dimension.height);
	}

	public void setStroke(float width) {
		g2d.setStroke(new java.awt.BasicStroke(uiobject.convertUnitToAbsolute(width)));
	}

	public void setFont(String font_name, float font_size) {
		if(font_name != null && font_name.length() > 0)
			this.font_name = font_name;
		this.font_size = font_size;
	}

	private void setFont() {
		Font font = new Font(font_name, 0, (int)uiobject.convertUnitToAbsolute(font_size));
		g2d.setFont(font);
	}

	public void line(float x1, float y1, float x2, float y2) {
		Point p1 = uiobject.getAbsolutePosition(x1, y1);
		Point p2 = uiobject.getAbsolutePosition(x2, y2);
		reg(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
		g2d.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}

	public void fillOval(float x, float y, float width, float height) {
		Point point = uiobject.getAbsolutePosition(x, y);
		Dimension dimension = uiobject.getAbsoluteDimension(width, height);
		reg(x, y, width, height);
		g2d.fillOval((int)point.x, (int)point.y, (int)dimension.width, (int)dimension.height);
	}

	public void oval(float x, float y, float width, float height) {
		// TODO implement lineWidth
		Point point = uiobject.getAbsolutePosition(x, y);
		Dimension dimension = uiobject.getAbsoluteDimension(width, height);
		reg(x, y, width, height);
		g2d.drawOval((int)point.x, (int)point.y, (int)dimension.width, (int)dimension.height);
	}

	public void bezier(float x, float y, Point[] points) {
		if(points.length == 0)
			return;

		Path2D.Float f = new Path2D.Float();
		Point point = uiobject.getAbsolutePosition(x, y);
		f.moveTo(point.x, point.y);

		if(points.length % 3 != 0)
			throw new RuntimeException();

		for(int i = 0; i < points.length; i += 3) {
			Point p1 = uiobject.getAbsolutePosition(points[i].x, points[i].y);
			Point p2 = uiobject.getAbsolutePosition(points[i+1].x, points[i+1].y);
			Point p3 = uiobject.getAbsolutePosition(points[i+2].x, points[i+2].y);

			f.curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
		}

		g2d.draw(f);
	}

	public void text(String text, float x, float y) {
		Point point = uiobject.getAbsolutePosition(x, y);
		setFont();
		g2d.drawString(text, point.x, point.y);
		reg(x, y - font_size, getTextWidth(text), font_size);
	}

	public float getTextWidth(String text) {
		setFont();
		return uiobject.convertAbsoluteToUnit(g2d.getFontMetrics().stringWidth(text));
	}

	public void empty(float x, float y, float width, float height) {
		/*
		 * Draw nothing, but increase the draw area. E.g to catch mouse clicks
		 * outside the drawn area.
		 */
		//Point point = uiobject.getAbsolutePixelPoint(x, y);
		//java.awt.Dimension dimension = uiobject.getPixelDimension(width, height);
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
