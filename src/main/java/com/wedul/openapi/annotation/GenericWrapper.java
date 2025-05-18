package com.wedul.openapi.annotation;

@CustomSchemeAnnotation(description = "generic wrapper")
public class GenericWrapper<T> {

    @CustomSchemeAnnotation(description = "wrapped value", example = "exampleValue", format = "generic type")
    private T value;
}