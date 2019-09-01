package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.analyzer.NetListUtil;
import net.merayen.elastic.backend.analyzer.NetListValidator;
import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.local.exceptions.SpawnLimitException;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.backend.nodes.UtilKt;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.ElasticMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.StatisticsReportMessage;
import net.merayen.elastic.util.AverageStat;

import java.util.*;

/**
 * For now, the Supervisor does not support adding and removal of nodes/ports, connections etc. You will need to clear and restart.
 * TODO this class does 2 things now. Should perhaps only schedule and run the nodes, as it is a supervisor.
 */
class Supervisor {
	private static final String CLASS_PATH = "net.merayen.elastic.backend.architectures.local.nodes.%s_%d.LNode";

	final NetList netlist;
	private final int sample_rate;
	final int buffer_size;
	long sampleCount = 0; // Global sample counter from

	/**
	 * Global samplePosition
	 */
	long samplePosition;

	private final NodeProperties node_properties;
	public final NetListUtil netlist_util;
	final ProcessorList processor_list = new ProcessorList();
	private final HashMap<String, LocalNode> nodes = new HashMap<>();
	private int session_id_counter;

	private boolean dead;

	private volatile List<LocalProcessor> scheduled = new ArrayList<>(); // LocalProcessors scheduled for execution in current frame
	private Set<Integer> dead_sessions = new HashSet<>(); // Sessions that will be killed after current process frame

	// Statistics
	private AverageStat<Long> process_time = new AverageStat<>(100);
	private long process_time_last;
	private long not_processing_time_last;
	private AverageStat<Long> not_processing_time = new AverageStat<>(1000);

	public Supervisor(NetList netlist, int sample_rate, int buffer_size) {
		this.netlist = netlist; // Our own, compiled NetList
		this.sample_rate = sample_rate;
		this.buffer_size = buffer_size;

		new NetListValidator(netlist); // Make sure the netlist is sane before we proceed

		node_properties = new NodeProperties(netlist);
		netlist_util = new NetListUtil(netlist);
		load();
	}

	private void load() {
		for(Node node : netlist.getNodes()) {
			LocalNode localnode = nodes.get(node.getID());

			if(localnode == null) { // LocalNode could already be loaded and assigned when doing tests
				String path = String.format(CLASS_PATH, node_properties.getName(node), node_properties.getVersion(node));
				try {
					@SuppressWarnings("unchecked")
					Class<? extends LocalNode> cls = (Class<? extends LocalNode>)Class.forName(path);
					localnode = cls.newInstance();
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					System.out.printf("Could not load Node %s\n", path);
					throw new RuntimeException(e);
				}

				nodes.put(node.getID(), localnode);
			}

			localnode.compiler_setInfo(this, node, sample_rate, buffer_size);
			localnode.init();

			// Apply any parameters
			BaseNodeData data = UtilKt.mapToLogicNodeData(node_properties.getName(node), node_properties.getVersion(node), node.properties);
			localnode.onParameter(data);
		}
	}

	void begin() {
		spawnMainSession();
	}

	/**
	 * Destroys everything and clears ourselves.
	 * Not much use of this instance after this call. Dispose it and create a new one.
	 */
	void clear() {
		dead = true;

		for(int session_id : new ArrayList<>(processor_list.getSessions()))
			removeSession(session_id);

		processor_list.clear();

		for(LocalNode lnode : nodes.values())
			lnode.onDestroy();

		nodes.clear();
	}

	public void handleMessage(ElasticMessage message) {
		if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			LocalNode localnode = nodes.get(m.node_id);
			localnode.onParameter(m.instance);
		}
	}

	/**
	 * Makes sure all outlets and inlets are connected.
	 * Call after LocalProcessors has been created and needs to be reconnected.
	 */
	private void wireUp(List<LocalProcessor> list) {
		for(LocalProcessor lp : list)
			lp.wireUp();
	}

	private void initProcessors(List<LocalProcessor> list) {
		for(LocalProcessor lp : list)
			lp.localnode.onSpawnProcessor(lp);

		for(LocalProcessor lp : list)
			lp.init();
	}

	int spawnSession(Node node, int sample_offset) throws SpawnLimitException {
		spawnSession(node, ++session_id_counter, sample_offset);
		return session_id_counter;
	}

	/**
	 * Spawns a new session.
	 * sample_offset is the offset into the process frame which the session gets spawned.
	 * If node is null, the topmost node's session gets started.
	 */
	synchronized private void spawnSession(Node node, int session_id, int sample_offset) throws SpawnLimitException {
		List<LocalNode> lnodes = new ArrayList<>(); // LocalNodes to create sessions on
		for(Node n : netlist.getNodes())
			if((node == null && node_properties.getParent(n) == null) || (node != null && node.equals(netlist_util.getParent(n))))
				lnodes.add(nodes.get(n.getID()));

		if(node != null)
			for(LocalNode ln : lnodes)
				if(processor_list.getSessions(ln).size() >= 128)
					throw new SpawnLimitException();

		processor_list.addSession(session_id);

		List<LocalProcessor> to_wire_up = new ArrayList<>();
		for(LocalNode ln : lnodes) {
			LocalProcessor lp = ln.spawnProcessor(session_id);
			to_wire_up.add(lp);
			processor_list.add(lp);
			schedule(lp);
		}

		wireUp(to_wire_up);
		initProcessors(to_wire_up);

		//System.out.println("Active sessions: " + processor_list.getSessions().size());
	}

	/**
	 * Spawns the main session, which is the topmost node, which again is responsible to spawn its children.
	 */
	private void spawnMainSession() {
		try {
			spawnSession(null, 0);
		} catch (SpawnLimitException e) {
			throw new RuntimeException("Should not happen");
		}
	}

	/**
	 * Called by a LocalProcessor to schedule it for processing.
	 */
	void schedule(LocalProcessor lp) {
		if(lp.localnode.supervisor != this)
			throw new RuntimeException("Should not happen");

		if(!scheduled.contains(lp))
			scheduled.add(lp);
	}

	/**
	 * Process a frame.
	 * Calls process() on all processors until everyone is satisfied.
	 * Returns a new ProcessMessage() with the output data.
	 */
	public synchronized ProcessMessage process(ProcessMessage message) {
		if (dead)
			throw new RuntimeException("Should not be called after clear()");

		long start = System.nanoTime();

		if(not_processing_time_last != 0)
			not_processing_time.add(start - not_processing_time_last);

		killSessions();

		// Reset all frame-state in the processors
		for(LocalProcessor x : processor_list)
			x.prepare(0);

		// First let the LocalNode process, so they may create their default sessions and schedule processors to process
		for (Node node : netlist.getNodes()) {
			LocalNode ln = nodes.get(node.getID());
			ln.process(message.getInput().get(node.getID()));
		}

		for (LocalProcessor lp : processor_list) {
			scheduled.add(lp);
			lp.process_time = 0;
		}

		while(!scheduled.isEmpty()) { // TODO implement logic that detects hanging processors
			List<LocalProcessor> to_process = scheduled;
			scheduled = new ArrayList<>();

			// Then let all the processors process
			for(LocalProcessor lp : to_process)
				lp.doProcess();
		}

		for(LocalProcessor lp : processor_list)
			lp.process_times.add(lp.process_time);

		// Make sure every processor has completely read and written to their buffers
		boolean failed = false;
		for(LocalProcessor lp : processor_list.getAllProcessors()) {
			if(!lp.frameFinished()) {
				failed = true;
				System.out.printf("Node failed to process: %s, session_id=%d. Frame has not been completely processed. Forgotten to increase Outlet.written / Inlet.read and called Outlet.push()?\n", lp.getClass(), lp.session_id);
			}
		}

		if(failed)
			System.out.println(Debug.debug(this));

		ProcessMessage response = new ProcessMessage();

		// Notify all LocalNodes that we have processed.
		for(Node node : netlist.getNodes()) {
			LocalNode ln = nodes.get(node.getID());

			ln.finishFrame();

			response.getOutput().put(ln.getID(), ln.outgoing);
		}

		killSessions();

		process_time.add(System.nanoTime() - start);

		if(/*Config.processor.debug.performance && */process_time_last < System.currentTimeMillis()) {
			process_time_last = System.currentTimeMillis() + 200;
			/*System.out.printf("Average process time: min=%.3fms, avg=%.3fms, max=%.3fms\n", process_time.getMin() / 1E6, process_time.getAvg() / 1E6, process_time.getMax() / 1E6);
			System.out.printf("Outside processing: min=%.3fms, avg=%.3fms, max=%.3fms\n", not_processing_time.getMin() / 1E6, not_processing_time.getAvg() / 1E6, not_processing_time.getMax() / 1E6);
			System.out.printf("Samples processed: avg=%.0fms\n", not_processing_time.getAvg());*/

			List<LocalNode> list = getLocalNodes();
			Map<String, StatisticsReportMessage.NodeStats> nodeStats = new HashMap<>();

			list.sort((a,b) -> (int)(b.getStatisticsMax() - a.getStatisticsMax()));
			for(int i = 0; i < 10 && i < list.size(); i++) {
				LocalNode ln = list.get(i);
				//System.out.printf("%d: sessions=%d, count=%d, avg=%.3fms, max=%.3fms: %s\n", i, ln.getProcessors().size(), ln.getStatisticsProcessCount(), ln.getStatisticsAvg() / 1E6, ln.getStatisticsMax() / 1E6, ln.getClass().getPackage().getName());
				nodeStats.put(ln.getID(), new StatisticsReportMessage.NodeStats(ln.getClass().getName(), 0f, (float)ln.getStatisticsAvg(), (float)ln.getStatisticsMax(), 0, ln.getStatisticsProcessCount()));
			}

			response.setStatisticsReportMessage(new StatisticsReportMessage(process_time.getAvg() / 1E9, process_time.getMax() / 1E9, not_processing_time.getAvg() / 1E9, nodeStats, buffer_size / (double) sample_rate));
		}

		not_processing_time_last = System.nanoTime();

		samplePosition += buffer_size;

		return response;
	}

	public LocalNode getLocalNode(String id) {
		return nodes.get(id);
	}

	public LocalProcessor getProcessor(LocalNode localnode, int session_id) {
		return processor_list.getProcessor(localnode, session_id);
	}

	public List<LocalProcessor> getProcessors(LocalNode localnode) {
		List<LocalProcessor> result = new ArrayList<>();
		for(LocalProcessor lp : processor_list.getAllProcessors())
			if(lp.localnode == localnode)
				result.add(lp);

		return result;
	}

	void removeSession(int session_id) { // TODO
		dead_sessions.add(session_id);
	}

	private void killSessions() {
		for(int session_id : dead_sessions) {
			for(LocalProcessor lp : processor_list.getProcessors(session_id)) {
				lp.onDestroy();
				scheduled.remove(lp);
			}

			processor_list.removeSession(session_id);
		}

		dead_sessions.clear();
	}

	private List<LocalNode> getLocalNodes() {
		return new ArrayList<>(nodes.values());
	}
}
