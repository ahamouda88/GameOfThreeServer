package com.challenge.got.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import com.challenge.got.rest.response.BaseResponse;
import com.challenge.got.rest.response.ResponseStatus;

/**
 * An interface that contains default common methods that might be used by
 * several controllers
 */
public interface ControllerCommonMethods {

	/**
	 * A default method that creates a Response entity
	 * 
	 * @param successHttpStatus
	 *            the http status in case data is not null
	 * @param failHttpStatus
	 *            the http status in case data is null
	 * @param errors
	 *            list of errors
	 * @param data
	 *            the data need to be returned
	 * @return a new {@link ResponseEntity} based on the given parameters
	 */
	default <T> ResponseEntity<BaseResponse<T>> createBaseResponse(HttpStatus successHttpStatus,
			HttpStatus failHttpStatus, List<String> errors, T data) {
		HttpStatus httpStatus;
		String message;
		if (CollectionUtils.isEmpty(errors)) {
			httpStatus = successHttpStatus;
			message = "Success";
		} else {
			httpStatus = failHttpStatus;
			message = "Failed";
		}

		ResponseStatus status = new ResponseStatus(httpStatus.value(), message, errors);
		return new ResponseEntity<>(new BaseResponse<>(status, data), httpStatus);
	}
}
