/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.updatechecker;

public interface ResponseHandler {
	
	/**
	 * Called when the updater finds a new version.
	 */
	void onUpdateFound(final String newVersion);
	
}