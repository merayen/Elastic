package net.merayen.elastic.backend.interfacing;

import net.merayen.elastic.backend.interfacing.platforms.oracle_java.DeviceScanner;

/**
 * Returns appropriate scanner for the current platform.
 */
public class Platform {
	public static DeviceScanner getPlatformScanner(AbstractDeviceScanner.Handler handler) {
		long scanTime = System.currentTimeMillis();
		DeviceScanner ds = new DeviceScanner(handler); // TODO actually check the platform and probably use introspection
		System.out.printf("Scanning interfaces took %.3f seconds\n", (System.currentTimeMillis() - scanTime) / 1000f);
		return ds;
	}
}
