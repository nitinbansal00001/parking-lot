package com.intuit.parkingLot.controller;

import com.intuit.parkingLot.dto.response.BaseResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseController {

    @ExceptionHandler(value = {Exception.class})
    public Object getExceptionResponse(Exception e) {
        return BaseResponse.buildFailure(e.getMessage());
    }
}
