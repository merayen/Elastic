package net.merayen.elastic.backend.storage.resource.types;

import java.util.Map;

import net.merayen.elastic.backend.storage.resource.Resource;

/**
 * Contains nothing
 * @author merayen
 *
 */
public class Null extends Resource {

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void onLoad() {}

}
