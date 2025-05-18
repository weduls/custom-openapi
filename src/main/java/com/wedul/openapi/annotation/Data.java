package com.wedul.openapi.annotation;

@CustomSchemeAnnotation(description = "data inner class")
public class Data {
    @CustomSchemeAnnotation(description = "field name in data", example = "exampleField", format = "string")
    private String fieldName;
}