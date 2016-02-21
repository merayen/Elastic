package net.merayen.merasynth.netlist.util;

import net.merayen.merasynth.net.util.flow.portmanager.PortManager;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.process.AbstractProcessor;
import net.merayen.merasynth.process.AudioProcessor;
import net.merayen.merasynth.process.ProcessorController;

/**
 * High level abstract node that most nodes should use and implement, if not special needs
 * are needed. Class does most of the usual functionality used by audio (and MIDI nodes). 
 */
public abstract class AudioNode<T extends AbstractProcessor> extends Node {

	protected ProcessorController<T> processor_controller;
	PortManager port_manager;

	public AudioNode(Supervisor supervisor, Class<T> processor_class) {
		super(supervisor);
		processor_controller = new ProcessorController<T>(this, processor_class, new ProcessorController.IHandler() {

			@Override
			public void onCreate(AbstractProcessor processor) {
				((AudioProcessor)processor).setPortManager(port_manager);
			}

		});
		port_manager = new PortManager(this);
	}

	public Stats getStats() {
		return new Stats(this);
	}

	/**
	 * Call this method in the onRemovePort() method.
	 */
	@Override
	public void removePort(String port_name) {
		super.removePort(port_name);
		port_manager.refreshPorts();
	}

	@Override
	/**
	 * Remember to call us if you override.
	 */
	protected void onCreatePort(String port_name) {
		port_manager.refreshPorts();
	}

	@Override
	/**
	 * Remember to call us if you override.
	 */
	protected void onRemovePort(String port_name) {
		port_manager.refreshPorts();
	}

	@Override
	protected void onReceive(String port_name, DataPacket dp) {
		processor_controller.handle(port_name, dp);
	}

	@Override
	protected void onRewire() {
		processor_controller.killAll(); // TODO Not sure how we should go with this, would be nice if processor checked state of port for every time
		port_manager.reset();
	}
}
