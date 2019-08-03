package com.challenge.got.exception.model;

/**
 * An exception which is thrown when the current player is not correct
 */
public class InvalidCurrentPlayerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidCurrentPlayerException(String message) {
		super(message);
	}
}
