package net.merayen.elastic.uinodes;

import net.merayen.elastic.backend.logicnodes.NodeRegistry;

import java.util.*;

public class UINodeInformation {
	private static final String UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.";

	private UINodeInformation() {}

	public static List<BaseInfo> getNodeInfos() {
		List<BaseInfo> result = new ArrayList<>();

		for(String p : NodeRegistry.INSTANCE.getNodes()) {
			try {
				result.add( (BaseInfo)Class.forName(UI_CLASS_PATH + p + ".Info").newInstance() );
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				System.out.printf("Could not retrieve Info-class for %s. Forgotten to create that class?\n", p);
			}
		}

		return result;
	}

	public static Set<String> getCategories() {
		Set<String> result = new HashSet<>();

		for(BaseInfo info : getNodeInfos())
			result.addAll(Arrays.asList(info.getCategories()));

		return result;
	}
}
