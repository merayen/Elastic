package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.pack.PackDict;

public abstract class Message extends Postmaster.Message {
	public abstract PackDict dump();
}
