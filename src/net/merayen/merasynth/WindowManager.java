package net.merayen.merasynth;

import java.util.ArrayList;

public class WindowManager {
	/*
	 * Manages multiple windows
	 */
	private ArrayList<Window> node_systems = new ArrayList<Window>();

	public Window create() {
		// TODO probably return a Window()-instance of some kind

		Window w = new Window();
		w.setHandler(new Window.Handler() {
			@Override
			public void onClose() {
				node_systems.remove(w);	
			}
		});

		return w;
	}
}
