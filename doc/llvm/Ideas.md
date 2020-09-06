# LLVM ideas

## Purpose
Get LLVM to dynamically compile the whole NetList down to a native binary that listens on a socket.

## Communication
Whenever the user changes the NetList by connecting, disconnecting a port, or adding/removing a node, the native binary
running will be stopped, new C-code will be generated, new native executable will be recompiled and then initialized
with initial data like audio samples, midi data etc via TCP socket.

When the user turns a knob on a node, that will be communicated with minimal data in the next process request, sent over
the TCP-socket.

### FrameData
The FrameData is sent to the native executable over a TCP socket every time it is requested to process a new frame (a
frame being e.g 256 samples of audio that is to be emitted by the native executable).

The FrameData can be empty if there is are no data updates in place for the native executable, like no MIDI score
changes, no knobs turned etc.

It is important we separate the node logic and the data, so that we can very quickly do e.g midi score changes without
having to recompile and reset anything.

## Multiple instances of nodes
Each node can have many instances, in different sessions. The Java-side of Elastic should not be aware of this and
should only see the incoming and outgoing node data. That data itself may contain data from all the different sessions
split up, but the Java-backend itself has no clue about it itself.