package com.mx.banobras.api.tokenizer.infraestructure.config.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDTO {
	  private int statusCode;
	  private Date timestamp;
	  private String message;
}
