package net.merayen.elastic.backend.logicnodes.list.poly_1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {
	private final int MAX_PORTS = 16;

	@Override
	protected void onCreate() {
		createLine();
	}

	@Override
	protected void onInit() {
		updateLines(false);
	}

	@Override
	protected void onParameterChange(String key, Object value) {}

	@Override
	protected void onData(Map<String, Object> data) {}

	@Override
	protected void onConnect(String port) {
		System.out.println("Poly got input connection on port " + port);
		createLine(); // Always have a line ready for the user to connect
	}

	private Set<Integer> getAllLines() {
		Set<Integer> in = new HashSet<>();
		Set<Integer> out = new HashSet<>();

		for(String port : getPorts()) {
			if(port.startsWith("input_"))
				in.add(Integer.parseInt(port.split("_")[1]));

			else if(port.startsWith("output_"))
				out.add(Integer.parseInt(port.split("_")[1]));
		}

		in.retainAll(out);
		return in;
	}

	private Set<Integer> getDisconnectedLines(Set<Integer> lines) {
		Set<String> ports = new HashSet<>(Arrays.asList(getPorts()));

		Set<Integer> result = new HashSet<>();
		for(int line : lines)
			if(!ports.contains("input_" + line) || !ports.contains("output_" + line))
				result.add(line);
			else if(!isConnected("input_" + line) && !isConnected("output_" + line))
				result.add(line);

		return result;
	}

	/**
	 * Adds and removes output ports depending on connected ports
	 */
	private void updateLines(boolean before_delete) {
		Set<String> ports = new HashSet<>(Arrays.asList(getPorts()));
		Set<Integer> lines = getAllLines();
		Set<Integer> disconnected = getDisconnectedLines(lines);

		// Remove all inputs and outputs that are not in use, leaving 1
		Iterator<Integer> iterator = disconnected.iterator();
		while(disconnected.size() > (before_delete ? 0 : 1)) {
			int line = iterator.next();
			System.out.println("Removing unused line " + line);
			if(ports.contains("input_" + line))
				removePort("input_" + line);
			if(ports.contains("output_" + line))
				removePort("output_" + line);

			iterator.remove();
		}

		if(disconnected.size() == 0)
			createLine();
	}

	private void createLine() {
		Set<Integer> lines = getAllLines();

		for(int i = 0; i < MAX_PORTS; i++) {
			if(!lines.contains(i)) {
				PortDefinition pd_in = new PortDefinition();
				pd_in.name = "input_" + i;
				createPort(pd_in);

				PortDefinition pd_out = new PortDefinition();
				pd_out.name = "output_" + i;
				pd_out.output = true;
				pd_out.format = Format.MIDI; // TODO need to dynamically change this!
				createPort(pd_out);
				System.out.println("Created line " + i);
				return;
			}
		}
	}

	@Override
	protected void onDisconnect(String port) {
		updateLines(true);
	}

	@Override
	protected void onRemove() {}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {}
}
