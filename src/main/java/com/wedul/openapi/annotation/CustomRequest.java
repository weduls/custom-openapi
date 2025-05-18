package com.wedul.openapi.annotation;

import java.util.List;
import java.util.Map;

@CustomSchemeAnnotation(description = "custom request class")
public class CustomRequest {

    @CustomSchemeAnnotation(description = "custom annotation name", example = "wedul", format = "String")
    private String name;

    @CustomSchemeAnnotation(description = "custom annotation age", example = "10", format = "int")
    private int age;

    @CustomSchemeAnnotation(description = "list of string", example = "[\"classA\", \"classB\"]", format = "array of string")
    private List<String> stringList;

    @CustomSchemeAnnotation(description = "data list example", example = "{\"fieldName\":\"exampleField\"}", format = "array of Data")
    private Data data;

    @CustomSchemeAnnotation(description = "generic string wrapper", example = "{\"value\":\"hello\"}", format = "GenericWrapper<String>")
    private GenericWrapper<String> wrappedString;

    @CustomSchemeAnnotation(description = "generic data wrapper", example = "{\"value\":{\"fieldName\":\"nestedField\"}}", format = "GenericWrapper<Data>")
    private GenericWrapper<Data> wrappedData;

    @CustomSchemeAnnotation(description = "metadata map", example = "{\"key1\": 1, \"key2\": 2}", format = "Map<String, Integer>")
    private Map<String, Integer> metadata;

}
