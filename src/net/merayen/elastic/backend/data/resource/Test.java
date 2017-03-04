package net.merayen.elastic.backend.data.resource;

public class Test {
	static void no() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		ResourceManager rm = new ResourceManager();
		Resource a = rm.create("revisions/4F2.json");
		a.data.put("file", true);
		rm.getTop().depends.add(a);

		Resource b = rm.create("audio/voff.wav");
		b.data.put("file", true);
		a.depends.add(b);

		Resource c = rm.create("to_be_deleted");
		c.depends.add(a); // Depends on a, but no one depends on us

		rm.tidy();

		if(rm.get("") == null)
			no();

		if(rm.get("audio/voff.wav") == null)
			no();

		if(rm.get("revisions/4F2.json") == null)
			no();

		if(rm.get("to_be_deleted") != null)
			no();
	}
}