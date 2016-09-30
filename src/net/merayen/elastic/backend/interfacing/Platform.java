package net.merayen.elastic.backend.interfacing;

import net.merayen.elastic.backend.interfacing.platforms.oracle_java.DeviceScanner;

/**
 * Returns appropriate scanner for the current platform.
 */
public class Platform {
	public static DeviceScanner getPlatformScanner(AbstractDeviceScanner.Handler handler) {
		return new DeviceScanner(handler); // TODO actually check the platform and probably use introspection
	}
}
