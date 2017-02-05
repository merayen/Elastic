package net.merayen.elastic.backend.storage;

import net.merayen.elastic.backend.resource.ResourceManager;

public class Test {
	public static void test() {
		FileSystemStorage fss = new FileSystemStorage("./TestProject.elp");
		ResourceManager rm = new ResourceManager(fss);
	}
}
