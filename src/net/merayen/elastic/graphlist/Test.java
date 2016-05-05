package net.merayen.elastic.graphlist;

import java.util.List;

public class Test {
	private static void nope() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		GList glist = new GList();
		GIterator giterator = new GIterator(glist);

		GObject[] å = new GObject[6];
		for(int i = 0; i < å.length; i++) {
			å[i] = glist.create();
			å[i].properties.put("no", i);
		}

		glist.add(å[0], å[1]); // Top object
		glist.add(å[1], å[2]);
		glist.add(å[0], å[3]);
		glist.add(å[4], å[5]);

		List<GObject> top_objects = giterator.getTopObjects();
		if(top_objects.size() != 2)
			nope();

		if(!top_objects.contains(å[0]))
			nope();

		if(!top_objects.contains(å[4]))
			nope();

		for(GObject g : giterator.getChildren(å[0]))
			System.out.println(g);

		List<GObject> non_connected = giterator.getNonConnected(å[3]);
		if(non_connected.size() != 2)
			nope();

		if(!non_connected.contains(å[4]) || !non_connected.contains(å[5]))
			nope();

		{
			class Omg {int i = 0;}
			Omg omg = new Omg();
			Object[] t = new Object[10];
			int STEP_DOWN = 1;
			int STEP_UP = 2;

			giterator.iterateFrom(å[0], new GIterator.Iterate() {
				
				@Override
				public void stepUp() {
					t[omg.i++] = STEP_UP;
				}
				
				@Override
				public void stepDown() {
					t[omg.i++] = STEP_DOWN;
				}
				
				@Override
				public void evaluate(GObject o) {
					t[omg.i++] = o;
				}
			});

			Object[] u = new Object[]{å[0], STEP_DOWN, å[1], STEP_DOWN, å[2], STEP_UP, å[3], STEP_UP};
			for(int i = 0 ; i < u.length; i++)
				if(!t[i].equals(u[i]))
					nope();
		}
	}
}
