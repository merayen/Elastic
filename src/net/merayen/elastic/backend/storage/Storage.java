package net.merayen.elastic.backend.storage;

public interface Storage {
	/**
	 * Returns a StorageView where files can be opened.
	 */
	public StorageView createView();

	/**
	 * Closes the storage, and all the StorageViews. All files related to this storage will be closed.
	 */
	public void close();
}
