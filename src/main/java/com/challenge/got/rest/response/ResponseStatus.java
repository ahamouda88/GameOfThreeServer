package com.challenge.got.rest.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A class that represents the status of the response
 */
@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(content = Include.NON_EMPTY)
public class ResponseStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	private List<String> errors;

	public ResponseStatus() {
		this(-1, null, null);
	}

	public ResponseStatus(int code, String message, List<String> errors) {
		this.code = code;
		this.message = message;
		this.errors = errors == null ? new ArrayList<>() : errors;
	}
}
