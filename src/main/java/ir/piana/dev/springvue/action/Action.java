package ir.piana.dev.springvue.action;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.function.Function;

public abstract class Action {
    public Function<RequestEntity, ResponseEntity> getField(String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = this.getClass().getField(fieldName);
        return (Function<RequestEntity, ResponseEntity>)field.get(this);
    }
}
