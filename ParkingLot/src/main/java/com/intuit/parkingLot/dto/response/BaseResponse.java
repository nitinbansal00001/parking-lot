package com.intuit.parkingLot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class BaseResponse {
    private Boolean success;
    private String msg;
    private Object data;

    public static BaseResponse buildSuccess(String msg, Object data) {
        return new BaseResponse(Boolean.TRUE, msg, data);
    }

    public static BaseResponse buildSuccess(Object data) {
        return new BaseResponse(Boolean.TRUE, null, data);
    }

    public static BaseResponse buildSuccess(String msg) {
        return new BaseResponse(Boolean.TRUE, msg, null);
    }

    public static BaseResponse buildFailure(String msg) {
        return new BaseResponse(Boolean.FALSE, msg, null);
    }
}
