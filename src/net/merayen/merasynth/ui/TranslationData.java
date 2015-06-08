package net.merayen.merasynth.ui;

public class TranslationData {
	public float x = 0, y = 0, z = 0; // Object's origo (relative to parent)
	public float scroll_x = 0, scroll_y = 0; // Offset position for the elements inside
	public float scale_x = 1, scale_y = 1; // Scale 1.0 is the same as max X/Y mapped to the window. Scale 2.0 requires 2.0 to meen fvdlsfjldkfgdjl
	public float rot_x = 0, rot_y = 0;
	public float width = 0, height = 0; // ??? TODO Fjerne, og heller detecte hitbox via Draw()?
	
	public TranslationData() {
		
	}
	
	public TranslationData(TranslationData t) {
		x = t.x;
		y = t.y;
		z = t.z;
		scroll_x = t.scroll_x;
		scroll_y = t.scroll_y;
		scale_x = t.scale_x;
		scale_y = t.scale_y;
		rot_x = t.rot_x;
		rot_y = t.rot_y;
		width = t.width;
		height = t.height;
	}
}
