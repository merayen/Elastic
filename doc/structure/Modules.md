# Modules

Elastic has 3 different modules that communicates with each other via asynchronous messages to decouple code.

## The intent behind the design decision

The idea behind this design decision is to decouple the modules from each other, allowing UI to never hang due to some non-UI process, or the audio-processing module to stop because of some UI-stuff.

The design decision makes implementing communications between the modules more time consuming, but hopefully this will make the application very fluid.

Another reason is that you can later on script everything, creating whole projects by just sending messages into the backend, and the UI will get updated and same with the backend, so you could technically create a full project that plays by just sending messages.

Hopefully this will also allow for multiplayer-usage of Elastic, but we will see. Perhaps not.

## The modules

We have 3 modules.

### UI

`net.merayen.elastic.ui`

Draws the UI and does the logic behind the UI-components, window management etc. Can have multiple implementations, as long as it supports the required messages.


### Logic

`net.merayen.elastic.backend.logicnodes`

This takes care of constraints and forwarding messages from UI to the audio processing module


### Audio processing

`net.merayen.elastic.backend.architectures`

Does all the audio/data processing. Runs in one or several threads and processes a single chunk of audio, which can for example be 512 samples of audio.

It can have multiple implementations. Perhaps one for OpenCL and one for the Java/CPU. Currently only Java/CPU. Not fully decided at the time of writing.
