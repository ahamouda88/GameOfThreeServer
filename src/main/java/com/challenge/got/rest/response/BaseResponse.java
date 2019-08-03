package com.challenge.got.rest.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * A class that represents the response from the API. It consists of the {@link ResponseStatus} and data
 */
@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(content = Include.NON_EMPTY)
public class BaseResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private ResponseStatus status;
	private T data;

	public BaseResponse() {
		this(new ResponseStatus(), null);
	}

	public BaseResponse(ResponseStatus status, T data) {
		this.status = status;
		this.data = data;
	}
}
