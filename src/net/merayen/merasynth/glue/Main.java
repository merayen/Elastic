package net.merayen.merasynth.glue;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.merasynth.client.signalgenerator.Glue;
import net.merayen.merasynth.glue.nodes.Top;

public class Main {
	
	public static Top load(String dump) {
		Context context = new Context();
		Top top = new Top(context);
		
		if(dump == null)
			top.addObject(new Glue(context));
		else {
			JSONObject dumpo;
			try {
				dumpo = (JSONObject)(new org.json.simple.parser.JSONParser().parse(dump));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException("NO!");
			}
			top.restore(dumpo);
		}
		
		return top;
	}
	
	public static void main(String slkfjdkl[]) {
		Top top = load(null);
		String dump = top.dump().toJSONString();
		System.out.println(dump);
		top = load(dump);
		dump = top.dump().toJSONString();
		System.out.println(dump);
	}
}
