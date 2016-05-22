package net.merayen.elastic.system.intercom;

/**
 * Sent from the architecture to request to be reloaded. Happens usually when
 * has been messaged for changes that it can't do on-the-fly, and needs to be
 * reloaded. XXX Hmm... Or maybe it should have a local NetList that it builds
 * by the messages it receives?
 */
public class ResetRequestMessage {

}
