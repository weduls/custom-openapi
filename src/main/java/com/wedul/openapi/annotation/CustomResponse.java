package com.wedul.openapi.annotation;

@CustomSchemeAnnotation
public class CustomResponse {

    @CustomSchemeAnnotation(description = "result Message", example = "this is result.", format = "String")
    private String resultMessage;

    public CustomResponse(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public CustomResponse() {

    }
}
