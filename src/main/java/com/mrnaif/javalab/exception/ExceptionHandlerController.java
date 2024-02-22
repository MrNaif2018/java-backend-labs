package com.mrnaif.javalab.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ExceptionDetails> resourceNotFound(
			ResourceNotFoundException e, WebRequest request) {
		ExceptionDetails details = new ExceptionDetails(
				new Date(),
				e.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ExceptionDetails> invalidRequest(
			InvalidRequestException e, WebRequest request) {
		ExceptionDetails details = new ExceptionDetails(
				new Date(),
				e.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
	}

}
