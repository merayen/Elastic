package net.merayen.merasynth.ui;

public class TranslationData {
	public float x = 0, y = 0, z = 0; // Object's origo (relative to parent)
	public float scroll_x = 0, scroll_y = 0; // Offset position for the elements inside
	public float scale_x = 1, scale_y = 1; // Scale 1.0 is the same as max X/Y mapped to the window. Scale 2.0 requires 2.0 to meen fvdlsfjldkfgdjl
	public float rot_x = 0, rot_y = 0;
	
	public TranslationData copy() {
		TranslationData r = new TranslationData();
		
		r.x = x;
		r.y = y;
		r.z = z;
		r.scroll_x = scroll_x;
		r.scroll_y = scroll_y;
		r.scale_x = r.scale_x;
		r.scale_y = r.scale_y;
		r.rot_x = r.rot_x;
		r.rot_y = r.rot_y;
		
		return r;
	}
}
