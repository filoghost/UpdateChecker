/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.updatechecker;

public interface ResponseHandler {
	
	/**
	 * Called when the updater finds a new version.
	 */
	void onUpdateFound(final String newVersion);
	
}