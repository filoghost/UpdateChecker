package me.filoghost.updatechecker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PluginVersionTest {

	@Test
	void testVersionParsing() throws InvalidVersionException {
		assertEquals("v1.0.0", new PluginVersion("Plugin 1.0.0").getFormattedVersion());
		assertEquals("v1.0.0", new PluginVersion("Plugin v1.0.0").getFormattedVersion());
		assertEquals("v1.0.0", new PluginVersion("Plugin v1..0..0").getFormattedVersion());
		assertEquals("v1.0.0", new PluginVersion("Plugin-1.0.0").getFormattedVersion());
		assertEquals("v1.0.0", new PluginVersion("Plugin v1.0.0-SNAPSHOT").getFormattedVersion());
		assertEquals("v1.0.0", new PluginVersion("Plugin v01.00.00").getFormattedVersion());
		assertEquals("v1", new PluginVersion("Plugin v1.a0.0").getFormattedVersion());
		assertEquals("v1", new PluginVersion("Plugin v1a.0.0").getFormattedVersion());
		assertEquals("v1.0", new PluginVersion("Plugin v1.0a.0").getFormattedVersion());
		assertEquals("v1", new PluginVersion("Plugin 1a.0.0").getFormattedVersion());
	}

	@Test
	void testInvalidVersion() {
		assertThrows(InvalidVersionException.class, () -> new PluginVersion("Plugin without version"));
		assertThrows(InvalidVersionException.class, () -> new PluginVersion("Plugin v1.99999999999999999999.0"));
	}

	@Test
	void testVersionComparison() throws InvalidVersionException {
		assertTrue(isNewerThan("2.0.0", "1.0.0"));
		assertTrue(isNewerThan("1.0.0", "1.0.0-SNAPSHOT"));
		assertFalse(isNewerThan("1.0.0", "1.0.0"));
		assertFalse(isNewerThan("1.0.0", "1"));
	}

	private boolean isNewerThan(String subject, String target) throws InvalidVersionException {
		return new PluginVersion(subject).isNewerThan(new PluginVersion(target));
	}

}