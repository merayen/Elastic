package net.merayen.elastic.system.actions;

import java.io.File;

import net.merayen.elastic.system.Action;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.system.intercom.ui.InitUIMessage;

/**
 * Loads a main project.
 */
public class LoadProject extends Action {

	private final String path;

	public LoadProject(String path) {
		this.path = path;
	}

	@Override
	protected void run() {
		//if(!new File(path).isDirectory())
		//	throw new RuntimeException("Project not found");

		system.end();

		system.sendMessageToUI(new InitUIMessage());

		system.sendMessageToBackend(new InitBackendMessage(44100, 16, 512, path));
	}
}
