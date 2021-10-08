# TODO
## Car engine synth
Creating engine sounds in synth.

- [x] X position line in XMap
- [ ] wave-node to support custom waveform again
	- [ ] Port the resampler algorithm? Use one in [C already](git@github.com:minorninth/libresample.git )?
- [ ] Derivation(?) node, outputting difference
	- This is for testing car reving
- [ ] Slew node, with customized slew rate for increasing and decreasing?
- [ ] Delay-filter
	- Multiple taps with individual amplitudes (...and feedback?)
	- May need a separate feedback line that gets fed into it?
