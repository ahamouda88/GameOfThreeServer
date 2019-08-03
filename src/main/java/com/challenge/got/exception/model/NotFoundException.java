package com.challenge.got.exception.model;

/**
 * An exception that is thrown when an entity is not found
 */
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
		super(message);
	}
}
