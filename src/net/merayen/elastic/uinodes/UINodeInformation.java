package net.merayen.elastic.uinodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.merayen.elastic.backend.logicnodes.Registry;

public class UINodeInformation {
	private static final String UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.";

	private UINodeInformation() {}

	public static List<BaseInfo> getNodeInfos() {
		List<BaseInfo> result = new ArrayList<>();

		for(String p : Registry.INSTANCE.getNodes()) {
			try {
				result.add( (BaseInfo)Class.forName(UI_CLASS_PATH + p + ".Info").newInstance() );
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static Set<String> getCategories() {
		Set<String> result = new HashSet<>();

		for(BaseInfo info : getNodeInfos())
			for(String category : info.getCategories())
				result.add(category);

		return result;
	}
}
