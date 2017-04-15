package net.merayen.elastic.system.intercom;

import java.util.Map;

import net.merayen.elastic.util.Postmaster;

public abstract class Message extends Postmaster.Message {
	public abstract Map<String, Object> dump();
}
