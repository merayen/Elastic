package net.merayen.elastic.system;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

import net.merayen.elastic.system.actions.LoadProject;
import net.merayen.elastic.system.actions.NewProject;
import net.merayen.elastic.system.intercom.backend.TidyProjectMessage;

public class Test {
	public static void test() {
		new Test();
	}

	private ElasticSystem system;

	private final static String PROJECT_PATH = new File("NewProject.elastic").getAbsolutePath(); 

	int fires;
	long start = System.currentTimeMillis();
	long ispinne;

	class roflmao {long t;}
	private Test() {
		system = new ElasticSystem();

		// Temporary XXX
		File file = new File(PROJECT_PATH);
		if(file.exists()) {
			try {
				Files.walk(Paths.get(PROJECT_PATH)).sorted(Comparator.reverseOrder()).forEach((x) -> x.toFile().delete());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		system.runAction(new NewProject(PROJECT_PATH));

		roflmao roflmao = new roflmao();
		roflmao.t = System.currentTimeMillis() + 1 * 2000;
		waitFor(() -> System.currentTimeMillis() > roflmao.t);

		// Create a file in the project path, which should get deleted by the tidy operation below
		system.backend.getEnvironment().project.data.storage.createView().writeFile("mappe/test.txt").write("Hei på deg".getBytes(Charset.forName("UTF-8")));

		// Now just run
		roflmao.t = System.currentTimeMillis() + 5000000;
		waitFor(() -> System.currentTimeMillis() > roflmao.t);

		// Tidy project
		system.sendMessageToBackend(new TidyProjectMessage());

		roflmao.t = System.currentTimeMillis() + 1000;
		waitFor(() -> System.currentTimeMillis() > roflmao.t);

		// Load the project
		system.runAction(new LoadProject(PROJECT_PATH));

		// Run "forever", project should be identical to the one created at the beginning
		roflmao.t = System.currentTimeMillis() + 1000 * 3600;
		waitFor(() -> System.currentTimeMillis() > roflmao.t);

		system.end();
	}

	interface Func {
		public boolean noe();
	}

	private void waitFor(Func func) {
		try {
			while(!func.noe()) {
				system.update();
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
