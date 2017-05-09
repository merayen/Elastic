package net.merayen.elastic.backend.context.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.context.Action;
import net.merayen.elastic.system.intercom.FinishResetNetListMessage;
import net.merayen.elastic.system.intercom.BeginResetNetListMessage;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster;

/**
 * Loads a project. Automatically done when initiating the backend.
 */
public class LoadProjectAction extends Action {
	@Override
	protected void run() {
		if(!new File(env.project.path).isDirectory())
			throw new RuntimeException("Project not found. Should not happen as it should have been created or already exist");

		List<Postmaster.Message> messages = new ArrayList<>();

		messages.add(new BeginResetNetListMessage());
		messages.addAll(NetListMessages.disassemble(env.project.data.getRawNetList()));
		messages.add(new FinishResetNetListMessage());

		backend_context.message_handler.sendToBackend(messages);
	}
}
