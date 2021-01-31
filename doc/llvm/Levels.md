# DSP executable levels
These are the levels the executable output from Elastic may support.

## Level 1
The core of the signal processing. No code for threading etc, only the code
that does the DSP work.

Implemented but need better separation from Level 2, so that we only need to recompile Level 1
when user does any changes. Level 2 code should then call the Level 1 for each frame to process.

Status: Implemented

## Level 2
A separate process than can be run under Elastic+JVM that has threading support.

Should be more static than Level 1, also, not compiled for each change the user does to the netlist.

Status: Implemented, but need better separation from Level 1 (own .c-file)

## Level 3
The generated program can communicate with MIDI and audio devices. Elastic+JVM is only used for the
UI part. We will also support the operating system's non-standard audio interface code.

This level should probably be our goal as we want minimal delay from the audio driver asking for a
buffer of audio until we can deliver it.

## Level 4
Make the generated executable be able to run standalone, with no dependency on Elastic.

Could target webassembly, Javascript etc. Could also be used as an audio engine for e.g web browser
games.