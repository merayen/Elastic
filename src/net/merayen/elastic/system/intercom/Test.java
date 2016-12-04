package net.merayen.elastic.system.intercom;

import java.util.Arrays;

import net.merayen.elastic.system.intercom.coop.ResourceListMessage;
import net.merayen.elastic.util.pack.PackDict;

public class Test {
	private Test() {}

	private static void no() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		{
			ResourceListMessage.ResourceList rl = new ResourceListMessage.ResourceList();
			rl.add(new ResourceListMessage.ResourceItem("id", "name", "hash".getBytes()));
			PackDict pd = new ResourceListMessage(rl).dump();

			ResourceListMessage m = new ResourceListMessage(pd);

			if(!m.resourcelist.get(0).id.equals("id"))
				no();

			if(!m.resourcelist.get(0).name.equals("name"))
				no();

			if(!Arrays.equals(m.resourcelist.get(0).hash, "hash".getBytes()))
				no();
		}
	}
}
