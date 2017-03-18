package net.merayen.elastic.system.actions;

import net.merayen.elastic.system.Action;

/**
 * Deletes resources and files if they do not have any dependencies.
 */
public class TidyProject extends Action {
	@Override
	protected void run() {
		getEnvironment();
	}

}
