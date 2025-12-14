package com.dat.backend_version_2.mapper.training;

import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier // Báo cho MapStruct biết đây là annotation để phân loại
@Target({ElementType.METHOD}) // Chỉ dùng trên hàm
@Retention(RetentionPolicy.CLASS) // Giữ lại đến lúc compile
public @interface NoClassSessions {
}
