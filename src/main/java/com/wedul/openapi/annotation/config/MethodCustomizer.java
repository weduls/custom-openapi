package com.wedul.openapi.annotation.config;

import com.wedul.openapi.annotation.CustomMethodAnnotation;
import com.wedul.openapi.annotation.CustomSchemeAnnotation;
import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodCustomizer implements OpenApiCustomizer {

    @Override
    public void customise(OpenAPI openApi) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages("com.wedul")
                        .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.SubTypes)
        );

        Set<Method> annotatedMethods = reflections.getMethodsAnnotatedWith(CustomMethodAnnotation.class);

        for (Method annotatedMethod : annotatedMethods) {
            CustomMethodAnnotation annotation = annotatedMethod.getAnnotation(CustomMethodAnnotation.class);

            Operation operation = new Operation();
            operation.setSummary(annotation.title());
            operation.setOperationId(annotatedMethod.getName());

            Parameter[] parameters = annotatedMethod.getParameters();
            Class<?> requestType = parameters[0].getType();

            Schema<?> schema = buildSchemaFromCustomAnnotation(requestType, openApi);

            RequestBody requestBody = new RequestBody()
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(schema)));
            operation.setRequestBody(requestBody);


            Class<?> returnType = annotatedMethod.getReturnType();
            Schema<?> responseSchema = buildSchemaFromCustomAnnotation(returnType, openApi);

            ApiResponse apiResResponse = new ApiResponse()
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(responseSchema)));

            operation.setRequestBody(requestBody);
            operation.setResponses(new ApiResponses().addApiResponse("200", apiResResponse));

            PathItem pathItem = new PathItem().get(operation);
            openApi.path(annotatedMethod.getName(), pathItem);
        }
    }

    public Schema<?> buildSchemaFromCustomAnnotation(Class<?> clazz, OpenAPI openApi) {
        Schema<?> schema = new ObjectSchema();

        CustomSchemeAnnotation classAnnotation = clazz.getAnnotation(CustomSchemeAnnotation.class);
        if (classAnnotation != null) {
            applyCustomAnnotationToSchema(schema, classAnnotation);
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            CustomSchemeAnnotation fieldAnnotation = field.getAnnotation(CustomSchemeAnnotation.class);
            if (fieldAnnotation != null) {
                Schema<?> fieldSchema = resolveFieldSchema(field.getGenericType(), fieldAnnotation, openApi);
                schema.addProperties(field.getName(), fieldSchema);
            }
        }
        openApi.getComponents().addSchemas(clazz.getSimpleName(), schema);
        return schema;
    }

    private Schema<?> resolveFieldSchema(Type type, CustomSchemeAnnotation annotation, OpenAPI openApi) {
        Schema<?> schema;

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();
            Type actual = pt.getActualTypeArguments()[0];

            if (raw == Map.class) {
                Type valueType = pt.getActualTypeArguments()[1];
                Schema<?> valueSchema = resolveFieldSchema(valueType, annotation, openApi);

                schema = new MapSchema().additionalProperties(valueSchema);
            } else if (raw == List.class || raw == Set.class) {
                schema = new ArraySchema().items(resolveFieldSchema(actual, annotation, openApi));
            } else {
                schema = resolveFieldSchema(actual, annotation, openApi);
            }
        } else if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            schema = PrimitiveType.createProperty(clazz);
            if (schema == null) {
                buildSchemaFromCustomAnnotation(clazz, openApi);
                schema = new Schema<>().$ref("#/components/schemas/" + clazz.getSimpleName());
            }
        } else {
            schema = new StringSchema();
        }

        applyCustomAnnotationToSchema(schema, annotation);
        return schema;
    }

    private void applyCustomAnnotationToSchema(Schema<?> schema, CustomSchemeAnnotation ann) {
        if (!ann.description().isEmpty()) schema.setDescription(ann.description());
        if (!ann.example().isEmpty()) schema.setExample(ann.example());
        if (!ann.format().isEmpty()) schema.setFormat(ann.format());
    }

}
