package net.merayen.elastic.system.intercom.coop;

import java.util.ArrayList;

import net.merayen.elastic.util.pack.*;

/**
 * Sent by the client to get a list of all the resources on the server.
 * Replied with a list of all resources, name, and a checksum.
 * 
 * Client should then compare that list with its own list and request
 * missing/mismatching resources using ResourcePullMessage()
 */
public class ResourceListMessage extends CoopMessage {
	public static class ResourceList extends ArrayList<ResourceItem> {
		private PackArray dump() {
			PackArray result = new PackArray();

			result.data = new PackType[size()];

			int i = 0;
			for(ResourceItem d : this) {
				result.data[i++] = d.dump();
			}

			return result;
		}
	}

	public static class ResourceItem {
		public final String id;
		public final String name;
		public final byte[] hash;

		public ResourceItem(String id, String name, byte[] hash) {
			this.id = id;
			this.name = name;
			this.hash = hash;
		}

		private ResourceItem(PackDict data) {
			this.id = ((PackString)data.data.get("id")).data;
			this.name = ((PackString)data.data.get("name")).data;
			this.hash = ((ByteArray)data.data.get("hash")).data;
		}

		private PackDict dump() {
			PackDict result = new PackDict();

			result.data.put("id", new PackString(id));
			result.data.put("name", new PackString(name));
			result.data.put("hash", new ByteArray(hash));

			return result;
		}
	}

	public final ResourceList resourcelist;

	public ResourceListMessage(ResourceList resourcelist) {
		this.resourcelist = resourcelist;
	}

	public ResourceListMessage() {
		resourcelist = null;
	}

	public ResourceListMessage(PackDict data) {
		resourcelist = new ResourceList();

		for(PackType d : ((PackArray)data.data.get("list")).data)
			resourcelist.add(new ResourceItem((PackDict)d));
	}

	@Override
	public PackDict dump() {
		PackDict result = new PackDict();

		result.data.put("list", resourcelist.dump());

		return result;
	}
}