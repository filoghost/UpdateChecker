/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.gmail.filoghost.updater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.primitives.Ints;

class PluginVersion {
	
	private static Pattern VERSION_PATTERN = Pattern.compile("v?([0-9\\.]+)");
	
	// The version extracted from a string, e.g. "MyPlugin v1.3.2" becomes [1, 3, 2]
	private int[] versionNumbers;
	private boolean isDevBuild;
	
	
	PluginVersion(String input) throws InvalidVersionException {
		if (input == null) {
			throw new InvalidVersionException("input was null");
		}
		
		Matcher matcher = VERSION_PATTERN.matcher(input);
		
		if (!matcher.find()) {
			throw new InvalidVersionException("version pattern not found in \"" + input + "\"");
		}
		
		// Get the first group of the matcher (without the "v")
		String version = matcher.group(1);
		
		// Replace multiple full stops (probably typos) with a single full stop, and split the version with them
		String[] versionParts = version.replaceAll("[\\.]{2,}", ".").split("\\.");
		
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
	 * @return true if this version is newer than the other, false if equal or older
	 */
	boolean isNewerThan(PluginVersion other) {
		int longest = Math.max(this.versionNumbers.length, other.versionNumbers.length);
		
		for (int i = 0; i < longest; i++) {
			int thisVersionPart = i < this.versionNumbers.length ? this.versionNumbers[i] : 0;
			int otherVersionPart = i < other.versionNumbers.length ? other.versionNumbers[i] : 0;
			int diff = thisVersionPart - otherVersionPart;
			
			if (diff > 0) {
				return true;
			} else if (diff < 0) {
				return false;
			}
			
			// Continue the loop until diff = 0
		}
		
		// If we get here, they're the same version, check dev builds.
		// This version is newer only if it's not a dev build and the other is.
		if (other.isDevBuild && !this.isDevBuild) {
			return true;
		}
		
		return false;
	}
	

	String getFormattedVersion() {
		return "v" + Ints.join(".", versionNumbers);
	}
	
}