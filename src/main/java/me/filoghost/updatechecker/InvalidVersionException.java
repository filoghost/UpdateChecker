/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.updatechecker;

class InvalidVersionException extends Exception {

	private static final long serialVersionUID = 1L;

	InvalidVersionException(String message) {
		super(message);
	}
	
}