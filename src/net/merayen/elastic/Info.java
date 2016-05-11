package net.merayen.elastic;

import java.util.ArrayList;
import java.util.List;

public final class Info {
	public final static int[] VERSION = new int[]{0,0,1};

	@SuppressWarnings("serial")
	public static List<Integer> getVersion() {
		return new ArrayList<Integer>() {{
			for(Integer i : VERSION)
				add(i);
		}};
	}
}
