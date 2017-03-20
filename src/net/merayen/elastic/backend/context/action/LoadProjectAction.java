package net.merayen.elastic.backend.context.action;

import java.io.File;

import net.merayen.elastic.backend.context.Action;

/**
 * Loads a project. Automatically done when initiating the backend.
 */
public class LoadProjectAction extends Action {
	@Override
	protected void run() {
		if(!new File(env.project.path).isDirectory())
			throw new RuntimeException("Project not found. Should not happen as it should have been created or already exist");

		// TODO load messages from the netlist and send them to the backend
		System.out.println(env.project.getNetList());
	}
}
