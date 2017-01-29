package net.merayen.elastic.backend.storage;

public class Test {
	public static void test() {
		FileSystemStorage fss = new FileSystemStorage("./TestProject.elp");
		fss.save();
	}
}
