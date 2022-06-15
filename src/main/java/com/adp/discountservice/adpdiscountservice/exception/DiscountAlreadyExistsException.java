package com.adp.discountservice.adpdiscountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class DiscountAlreadyExistsException extends RuntimeException {
    public DiscountAlreadyExistsException(String msg) {
        super(msg);
    }
}
