package net.merayen.merasynth.ui.objects.components;

import java.awt.Graphics2D;

import net.merayen.merasynth.ui.objects.UIObject;

public class CircularSlider extends UIObject {

	@Override
	protected void onDraw(Graphics2D g) {
		// TODO Auto-generated method stub
		draw.fillOval(0, 0, 1f, 1f);
	}
}
