# Message system

## The components
Elastic has basically 3 components that send messages to each other.

### Backend
The backend is the main component where all the messages gets sent through and directed. It is always running as long as Elastic is running. It keep tracks of:
- NetList - Where all the nodes and their information is centrally stored
- Logic nodes - All nodes are defined here and has simple logic as defining ports, forwarding node data between UI and DSP
- Message routing - Sends messages to DSP and UI

### UI
There can be multiple implementations of the user interface. The built-in one is written in Java using Swing (perhaps OpenGL in the future).

### DSP
This component does the actual audio and data processing. The reference implementation is written in Java, but may in the future be implemented in Rust and/or using LLVM (JIT)

### External
The DSP-component and UI-component may be swapped out with something else by routing messages differently from the backend.

The external component can for example be a web browser showing the UI or a separate webservice doing the DSP.

## Booting Elastic

### Start a new project
1. ElasticSystem class gets initialized, but no modules.
2. Message NewProjectMessage(projectPath='/home/someone/My interesting take on things.elastic') gets sent into ElasticSystem
3. ElasticSystem starts the backend