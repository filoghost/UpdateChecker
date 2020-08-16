/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.updatechecker;

class InvalidVersionException extends Exception {

	private static final long serialVersionUID = 1L;

	InvalidVersionException(String message) {
		super(message);
	}
	
}