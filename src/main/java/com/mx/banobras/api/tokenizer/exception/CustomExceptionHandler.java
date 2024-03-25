package com.mx.banobras.api.tokenizer.exception;



import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		System.out.println("MessageNotReadable");
		
		Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("typeExeption", "handleHttpMessageNotReadable");
	    body.put("status", status.value());
	    
	    //Get all errors
	    String errors = "Exception APITOKEN: handleHttpMessageNotReadable :::" +  ex.getMessage();
	
	    body.put("errors", errors);
		
		return new ResponseEntity<Object>(body, headers, status);
	
	}
	
	
	
	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<Object> runtimeException(
			RuntimeException ex, WebRequest request) {

	   System.out.println("RuntimeException");
	    Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("status", "400");
	    //Get all errors
	    String errors = "RuntimeException APITOKEN:  :::"+ ex.getMessage();
	    System.out.println("RuntimeException2");
	    body.put("errors", errors);
	    return new ResponseEntity<Object>(
	    		body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
	
	
	

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
	  MethodArgumentTypeMismatchException ex, WebRequest request) {

	   System.out.println("TypeMismatc");
	    Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("status", "400");
	    //Get all errors
	    String errors = "MethodArgumentTypeMismatchException APITOKEN:  :::"+ ex.getMessage();
	
	    body.put("errors", findDataErrorJson(errors));
	    return new ResponseEntity<Object>(
	    		body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
	
//	//MGM
	@ExceptionHandler({ ResourceAccessException.class })
	public ResponseEntity<Object> resourceAccessException(ResourceAccessException ex, WebRequest request) {
		System.out.println("resourceAccessException");
		Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("status", "400");
	    //Get all errors
	    
	    //System.out.println("message: " + ex.getMessage()+ "ResourceAccessException:  APITOKEN :::"+ ex.getMessage());
	   String errors = ex.getMessage()+ "ResourceAccessException: APITOKEN :::"+ ex.getMessage();
	
	    //body.put("errors", findDataErrorJson(errors));
	    body.put("errors", errors);
	    return new ResponseEntity<Object>(
	      body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
		
	@ExceptionHandler({ ExpiredJwtException.class })
	public ResponseEntity<Object> expiredJwtException(ExpiredJwtException ex, WebRequest request) {
		System.out.println("resourceAccessException");
		Map<String, Object> body = new LinkedHashMap<>();
	    String errors = "AAPI ExpiredJwtException::" +ex.getMessage();
	    body.put("errors", errors);
	    body.put("status", "403");
	    return new ResponseEntity<Object>(
	      body, new HttpHeaders(), HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler({ MalformedJwtException.class })
	public ResponseEntity<Object> malformedJwtException(MalformedJwtException ex, WebRequest request) {
		System.out.println("resourceAccessException");
		Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("status", "403");
	    //Get all errors
	    
	    System.out.println("MalformedJwtException message: " + ex.getMessage());
	    String errors = "MalformedJwtException:  APITOKEN :::"+ ex.getMessage();
	
	    //body.put("errors", findDataErrorJson(errors));
	    body.put("errors", errors);
	    return new ResponseEntity<Object>(
	      body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<Object> malformedJwtException(NoSuchElementException ex, WebRequest request) {
		System.out.println("NoSuchElementException");
		Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("status", "403");
	    //Get all errors
	    
	    System.out.println("NoSuchElementException message: " + ex.getMessage());
	    String errors = "NoSuchElementException:  APITOKEN :::"+ "No hay datos";
	
	    //body.put("errors", findDataErrorJson(errors));
	    body.put("errors", errors);
	    return new ResponseEntity<Object>(
	      body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		System.out.println("handleAll");
		Map<String, Object> body = new LinkedHashMap<>();
	    body.put("timestamp", new Date());
	    body.put("status", "400");
	    //Get all errors
	    
	    System.out.println("message Exception:  APITOKEN :::"+ ex.getMessage());
	    String errors = "Exception:  APITOKEN :::"+ ex.getMessage();
	
	    //body.put("errors", findDataErrorJson(errors));
	    body.put("errors", errors);
	    return new ResponseEntity<Object>(
	      body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	
	
	private String findDataErrorJson(String dataError) {
		String iniString = "[";
		String finString = "]";
		
		try {
		
		int ii = dataError.lastIndexOf(iniString);
		int ff = dataError.lastIndexOf(finString);
		
		//dataError = "El parametro: " + dataError.substring(ii+2, ff-1) + ", no corresponde al tipo esperado."; ;
		dataError = "El parametro: " + dataError.substring(ii+2, ff-1) + ", no corresponde al tipo esperado."; ;
		}catch (Exception ex) {
			dataError = "El parametro de entrada no corresponde al esperado.";
		}
		
		return dataError;
		
	}
	

}
