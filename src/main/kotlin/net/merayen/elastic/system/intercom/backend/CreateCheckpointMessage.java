package net.merayen.elastic.system.intercom.backend;

/**
 * Message sent from the UI to request doing a checkpoint (store a new
 * revision). Typically happens when user like drag-drops audio into Elastic, or
 * a parameter in a node has been changed, and is such a change that it should
 * be stored as a checkpoint.
 */
public class CreateCheckpointMessage extends BackendMessage {}
