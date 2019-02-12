package net.merayen.elastic.backend.data.dependencygraph;

public class Test {
	static void no() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		DependencyGraph rm = new DependencyGraph();
		DependencyItem a = rm.create("revisions/4F2.json");
		a.getData().put("file", true);
		rm.getTop().getDependsOn().add(a);

		DependencyItem b = rm.create("audio/voff.wav");
		b.getData().put("file", true);
		a.getDependsOn().add(b);

		DependencyItem c = rm.create("to_be_deleted");
		c.getDependsOn().add(a); // Depends on a, but no one depends on us

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