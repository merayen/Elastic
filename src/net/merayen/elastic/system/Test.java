package net.merayen.elastic.system;

import java.io.File;

import net.merayen.elastic.system.actions.LoadProject;
import net.merayen.elastic.system.actions.NewProject;
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage;
import net.merayen.elastic.system.intercom.backend.StartBackendMessage;

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
		/*try {
			List<Number> test = ((List<Number>)new org.json.simple.parser.JSONParser().parse("[1,2,3,4,5.5,\"hei\"]"));
			System.out.println(test.get(5).doubleValue());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(5+6 > 6)
			return;*/

		system = new ElasticSystem();

		// Temporary XXX
		File file = new File(PROJECT_PATH);
		if(file.exists()) {
			/*try {
				Files.walk(Paths.get(PROJECT_PATH)).sorted(Comparator.reverseOrder()).forEach((x) -> x.toFile().delete());
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			// Load the project
			system.runAction(new LoadProject(PROJECT_PATH));
		} else {
			system.runAction(new NewProject(PROJECT_PATH));
		}

		roflmao roflmao = new roflmao();

		system.sendMessageToBackend(new StartBackendMessage());

		// Run "forever", project should be identical to the one created at the beginning
		while(true) {
			roflmao.t = System.currentTimeMillis() + 1000 * 10;
			waitFor(() -> System.currentTimeMillis() > roflmao.t);
			System.out.println("Saving checkpoint");
			system.sendMessageToBackend(new CreateCheckpointMessage());
		}
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
