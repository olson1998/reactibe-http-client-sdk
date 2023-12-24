package com.github.olson1998.http.serialization;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Getter
@ToString
@EqualsAndHashCode
public abstract class ResponseMapping<T> {

    private final Type pojoType;

    public ResponseMapping() {
        Type thisClass =this.getClass().getGenericSuperclass();
        if (thisClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.pojoType = ((ParameterizedType) thisClass).getActualTypeArguments()[0];
        }
    }
}
