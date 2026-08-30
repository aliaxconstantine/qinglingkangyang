package com.aliax.qlky.aop.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // 注解应用到方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时保留
public @interface ValidateParam {
    String[] propertyNames();  // 要校验的属性名数组
    String[] regex() default {};  // 对应的正则表达式数组，默认空表示不校验
    String[] errorMessages() default {};
}
