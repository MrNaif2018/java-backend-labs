package com.mrnaif.javalab.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerControllerTest {

  @Autowired private ExceptionHandlerController exceptions;

  @BeforeEach
  public void setUp() {
    exceptions = new ExceptionHandlerController();
  }

  @Test
  void handleInternalServerError() {
    RuntimeException exception = new RuntimeException("Test exception");
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionDetails> result = exceptions.handleAllExceptions(exception, webRequest);

    assertEquals("Test exception", result.getBody().getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
  }

  @Test
  void handleResourceNotFound() {
    ResourceNotFoundException exception = new ResourceNotFoundException("Test exception");
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionDetails> result = exceptions.resourceNotFound(exception, webRequest);

    assertEquals("Test exception", result.getBody().getMessage());
    assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
  }

  @Test
  void handleInvalidRequest() {
    InvalidRequestException exception = new InvalidRequestException("Test exception");
    MockHttpServletRequest request = new MockHttpServletRequest();
    WebRequest webRequest = new ServletWebRequest(request);
    ResponseEntity<ExceptionDetails> result = exceptions.invalidRequest(exception, webRequest);

    assertEquals("Test exception", result.getBody().getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
  }
}
