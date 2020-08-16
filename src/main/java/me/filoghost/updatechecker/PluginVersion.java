/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.updatechecker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

class PluginVersion {
	
	private static final Pattern VERSION_PATTERN = Pattern.compile("v?([0-9.]+)");
	
	// The version extracted from a string, e.g. "MyPlugin v1.3.2" becomes [1, 3, 2]
	private final int[] versionNumbers;
	private final boolean isDevBuild;
	
	
	protected PluginVersion(String input) throws InvalidVersionException {
		if (input == null) {
			throw new InvalidVersionException("input was null");
		}
		
		Matcher matcher = VERSION_PATTERN.matcher(input);
		
		if (!matcher.find()) {
			throw new InvalidVersionException("version pattern not found in \"" + input + "\"");
		}
		
		// Get the first group of the matcher (without the "v")
		String version = matcher.group(1);
		
		// Split the version parts by full stops (multiple consecutive allowed)
		String[] versionParts = version.split("\\.+");
		
		// Convert the strings to integers in order to compare them
		this.versionNumbers = new int[versionParts.length];
		for (int i = 0; i < versionParts.length; i++) {
			try {
				this.versionNumbers[i] = Integer.parseInt(versionParts[i]);
			} catch (NumberFormatException e) {
				throw new InvalidVersionException("invalid number in \"" + input + "\"");
			}
		}
		
		this.isDevBuild = input.contains("SNAPSHOT");
	}
	
	
	/**
	 * Compares this version with another version, using the array "versionNumbers".
	 * Examples:
	 * v1.12 is newer than v1.2 ([1, 12] is newer than [1, 2])
	 * v2.01 is equal to v2.1 ([2, 1] is equal to [2, 1])
	 * 
	 * @return NEWER if this version is newer than the other, OLDER if this version is older than the other, otherwise EQUAL.
	 */
	public CompareResult compareTo(PluginVersion other) {
		int longest = Math.max(this.versionNumbers.length, other.versionNumbers.length);
		
		for (int i = 0; i < longest; i++) {
			int thisVersionPart = i < this.versionNumbers.length ? this.versionNumbers[i] : 0;
			int otherVersionPart = i < other.versionNumbers.length ? other.versionNumbers[i] : 0;
			int diff = thisVersionPart - otherVersionPart;
			
			if (diff > 0) {
				return CompareResult.NEWER;
			} else if (diff < 0) {
				return CompareResult.OLDER;
			}
			
			// Continue the loop until diff = 0
		}
		
		// If we get here, they're the same version, check dev builds.
		// This version is newer only if it's not a dev build and the other is.
		if (other.isDevBuild && !this.isDevBuild) {
			return CompareResult.NEWER;
		} else if (!other.isDevBuild && this.isDevBuild) {
			return CompareResult.OLDER;
		}
		
		return CompareResult.EQUAL;
	}
	

	protected String getFormattedVersion() {
		return "v" + Ints.join(".", versionNumbers);
	}

}