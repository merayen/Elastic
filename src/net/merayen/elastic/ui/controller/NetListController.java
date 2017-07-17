package net.merayen.elastic.ui.controller;

import org.json.simple.JSONObject;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.util.NetListMessages;
import net.merayen.elastic.util.Postmaster.Message;

public class NetListController extends Controller {
	public final NetList netlist;

	public NetListController(Gate gate) {
		super(gate);
		this.netlist = new NetList(); // UI's own NetList, made by the messages sent to us
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onMessageFromBackend(Message message) {
		NetListMessages.apply(netlist, message);
	}

	@Override
	protected void onMessageFromUI(Message message) {}

	@Override
	protected JSONObject onDump() {
		return null;
	}

	@Override
	protected void onRestore(JSONObject obj) {}
}
