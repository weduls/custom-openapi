package com.wedul.openapi.annotation;

public class CustomMethodImpl implements CustomMethod {

    @Override
    @CustomMethodAnnotation(title = "test method")
    public CustomResponse test(CustomRequest request) {
        return new CustomResponse();
    }
}
