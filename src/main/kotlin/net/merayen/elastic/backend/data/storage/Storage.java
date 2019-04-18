package net.merayen.elastic.backend.data.storage;

public interface Storage {
	/**
	 * Returns a StorageView where files can be opened.
	 */
	StorageView createView();

	/**
	 * Closes the storage, and all the StorageViews. All files related to this storage will be closed.
	 */
	void close();
}
