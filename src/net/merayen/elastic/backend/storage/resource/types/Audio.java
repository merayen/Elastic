package net.merayen.elastic.backend.storage.resource.types;

import net.merayen.elastic.backend.storage.resource.Resource;

/**
 * Represents audio data.
 * TODO should take care of reading audio data and represent it raw.
 */
public class Audio extends Resource {

	public float[/* channel no */][/* sample index */] data;

	@Override
	public int getSize() {
		int sum = 0;
		if(data != null)
			for(float[] d : data)
				if(d != null)
					sum += d.length;

		return sum * 4;
	}

	@Override
	protected void onLoad() {}
}
