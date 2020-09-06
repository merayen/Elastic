# Variable speed
We want variable speed. Like, if we have a sample-node, and we want to speed up different parts of the sample, we want some possibility to speed up or slow down the audio clip. Preferably using nodes, so that generic math can be used to define what speed on the different parts.

## Proposal

### 1. Group-node inside sample-node
Here we can control the speed of the sample-node by inserting nodes into its "speed"-group that is hosted by the sample node.

```
|= Sample =======================================
|                                               |
|                                         Audio +--
|   |= Group ================================|  |
|   |                                        |  |
|   | |= In ===|          |= Out ========|   |  |
|   | |  Audio +-- .... --+ Speed        |   |  |
|   | |        |          |              |   |  |
|   | |========|          |==============|   |  |
|   |                                        |  |
|   |========================================|  |
|                                               |
|================================================
```

...where it is the Out-node with the port "Speed" that defines the speed of the sample all the way. User can then easily insert nodes in between to control the speed e.g by amplitude, pitch or something else.

The time inside the group in sample-node, reads the audio raw, without any speed changes. Though, if the playing speed is 200%, the group in the sample-node will run double the samples than outside the sample-node.

The sample-node will also "look-a-head", meaning if speed out is `150%`, and one buffer has been processed, e.g 256 samples, the sample-node will process 512 samples to retrieve speed data.

#### Pros
- Speed can be analyzed and changed before it gets actually sent out of the sample-node
- Adding a graph lfo inside to control the speed, will itself follow the speed it outputs
- The group inside sample-node could make the UI inside available directly on the sampler, e.g, a graph lfo will be shown
            
#### Cons
- There is no way to manipulate the speed from nodes outside the inner sample-group node
    - We might want to support both this proposal and the second one, to allow managing speed in both ways
- Speeds can never go below a certain amount, which actually may fit the resampler?
    - Could allow for e.g minimum 10% speed, and 1000% max speed?


### 2. Speed-port
The sample-node has a speed-port that takes a signal with value 0 to e.g 10, 0 is no playback, and 1 is normal playback.

```
 |= Sample ===|
 + Speed      |
 |            |
 |============|
```

#### Pros
- Can very easily change the speed of the audio played back

#### Cons
- Cannot control the speed of the audio by e.g its content


### 3. Poly-node (too much work)
Implement speed and resampling into poly-node.

```
|= Poly ===============================|
|                                      |
| |= In ====|          |= Out ===|     |
| |    Midi +-- .... --+ Audio   |     |
| |         |        --+ Speed   |     |
| |=========|          |=========|     |
|                                      |
|======================================|
```

#### Implementation
- Poly node divides the bufferSize by `2*2*2`, so 512 sample frames will be 64 frames, allowing for speed reduction to `100% / (2*2*2) = 12.5%`
    - This could perhaps be configurable
    - Will probably need to rework the buffering, so that nodes can have individual buffer-sizes on each level, which sounds like work...
- As poly-node needs to push data to the in-node's midi-port, and as poly does not know the speed set for that frame (it needs to be calculated afterwards), it is impossible for the poly-node to "resample" the ingoing midi. If the speed is 25%, all the midi data will be played in the first of four frames, and then again in the fifth frame of the eight

