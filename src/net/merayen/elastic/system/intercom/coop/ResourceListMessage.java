package net.merayen.elastic.system.intercom.coop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sent by the client to get a list of all the resources on the server.
 * Replied with a list of all resources, name, and a checksum.
 * 
 * Client should then compare that list with its own list and request
 * missing/mismatching resources using ResourcePullMessage()
 */
public class ResourceListMessage extends CoopMessage {
	@SuppressWarnings("serial")
	public static class ResourceList extends ArrayList<ResourceItem> {
		private List<Object> dump() {
			List<Object> result = new ArrayList<>();

			for(ResourceItem d : this)
				result.add(d.dump());

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

		private ResourceItem(Map<String, Object> data) {
			this.id = (String)data.get("id");
			this.name = (String)data.get("name");
			this.hash = (byte[])data.get("hash");
		}

		private Map<String, Object> dump() {
			Map<String, Object> result = new HashMap<>();

			result.put("id", id);
			result.put("name", name);
			result.put("hash", hash);

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

	@SuppressWarnings("unchecked")
	public ResourceListMessage(Map<String, Object> data) {
		resourcelist = new ResourceList();

		for(Object o : (List<Object>)data.get("list"))
			resourcelist.add(new ResourceItem((Map<String, Object>)o));
	}

	@Override
	public Map<String, Object> dump() {
		Map<String, Object> result = new HashMap<>();

		result.put("list", resourcelist.dump());

		return result;
	}
}