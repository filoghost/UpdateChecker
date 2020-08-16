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
		assertEquals(CompareResult.NEWER, compare("2.0.0", "1.0.0"));
		assertEquals(CompareResult.NEWER, compare("1.0.0", "1.0.0-SNAPSHOT"));
		assertEquals(CompareResult.NEWER, compare("1.0.2", "1.0.1"));
		assertEquals(CompareResult.EQUAL, compare("1.0.0", "1.0.0"));
		assertEquals(CompareResult.EQUAL, compare("1.0.0", "1"));
		assertEquals(CompareResult.OLDER, compare("1.0.0", "2.0.0"));
		assertEquals(CompareResult.OLDER, compare("1.0.0-SNAPSHOT", "1.0.0"));
		assertEquals(CompareResult.OLDER, compare("1.0.1", "1.0.2"));
	}

	private CompareResult compare(String subject, String target) throws InvalidVersionException {
		return new PluginVersion(subject).compareTo(new PluginVersion(target));
	}

}