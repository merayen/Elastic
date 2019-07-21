# How to implement a new node

## Steps
We will create a node that adds two signals together. We will call it "mynode".

### Step 1
Create the logic node, path: `net.merayen.elastic.backend.logicnodes.list.mynode_1.LogicNode`
```kotlin
class LogicNode : BaseLogicNode() {
	override fun onParameterChange(instance: BaseNodeData?) {
		updateProperties(instance) // Accept all incoming parameters
	}

	override fun onCreate() {
		createPort(InputPortDefinition("a")) // Create an input port called "a"
		createPort(InputPortDefinition("b")) // Create an input port called "b"
	}

	override fun onInit() {}
	override fun onData(data: NodeDataMessage?) {}
	override fun onConnect(port: String?) {}
	override fun onDisconnect(port: String?) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData?) {}
}
```

Node is in this case called "mynode_1", where "mynode" is the actual name, and "1" is the version of it.

(TODO: when to increase version of a node)

### Step 2
Add the logic node to the registry, in: `net.merayen.elastic.backend.logicnodes.NodeRegistry`

### Step 3
Create the UI-part of the node.
Every node needs a UI component that represents it.

```kotlin
class UI : UINode() {
	override fun onInit() {
		super.onInit()

		layoutWidth = 200f
		layoutHeight = 300f
	}

	override fun onCreatePort(port: UIPort) {}
	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeData) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(instance: BaseNodeData) {}
}
```

### Step 4
Create the DSP backend for the node.