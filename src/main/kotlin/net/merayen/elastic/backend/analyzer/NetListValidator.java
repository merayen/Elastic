package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Goes through a netlist and validates it.
 */
public class NetListValidator {
	@SuppressWarnings("serial")
	public static class ValidationError extends RuntimeException {
		public ValidationError(String text) {
			super(text);
		}
	}

	private final NetList netlist;
	private final NodeProperties node_properties;

	public NetListValidator(NetList netlist) {
		this.netlist = netlist;
		this.node_properties = new NodeProperties(netlist);

		validateSingleTop();
		validateParentLinks();
		validateIsolation();
	}

	/**
	 * Ensures there is only one node at the top.
	 * Otherwise, we can't figure out which the topmost group is.
	 */
	private void validateSingleTop() {
		int nodes_on_tops_on_top = 0;
		for (Node node : netlist.getNodes())
			if (node_properties.getParent(node) == null)
				if (++nodes_on_tops_on_top > 1)
					throw new ValidationError("Multiple groups at top");
	}

	/**
	 * Validates that all parent links are intact.
	 */
	private void validateParentLinks() {
		Set<String> parents = new HashSet<>();
		for (Node node : netlist.getNodes())
			if (node_properties.getParent(node) != null)
				parents.add(node_properties.getParent(node));

		String c_id = null;
		try {
			for (String id : parents)
				netlist.getNode(c_id = id);
		} catch (RuntimeException e) {
			throw new ValidationError("Could not find parent node with ID " + c_id);
		}
	}

	/**
	 * Validates that no groups are connected to each other.
	 */
	private void validateIsolation() {
		for (Line line : netlist.getLines())
			if (!node_properties.getParent(line.node_a).equals(node_properties.getParent(line.node_b)))
				throw new ValidationError("Connection between groups not allowed: " + line);
	}
}
