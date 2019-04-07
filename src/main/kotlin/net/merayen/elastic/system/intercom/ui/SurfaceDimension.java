package net.merayen.elastic.system.intercom.ui;

/**
 * Set the dimension of the surface, in pixels.
 */
public class SurfaceDimension {
	public final String id;
	public final int width, height;

	public SurfaceDimension(String id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}
}
