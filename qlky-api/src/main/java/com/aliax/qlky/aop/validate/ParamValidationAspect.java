package com.aliax.qlky.aop.validate;

import cn.hutool.core.util.ObjectUtil;
import com.aliax.qlky.exception.RequestParamException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Aspect
@Component
public class ParamValidationAspect {

    @Pointcut("@annotation(ValidateParam)")  // 切点：拦截所有标注了 @ValidateParam 注解的方法
    public void validateParamPointcut() {}

    @Before("validateParamPointcut()")  // 在方法执行前进行参数校验
    public void validateParamsBeforeMethod(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ValidateParam validateParam = method.getAnnotation(ValidateParam.class);

        String[] propertyNames = validateParam.propertyNames();  // 获取属性名数组
        String[] regexArray = validateParam.regex();  // 获取正则表达式数组
        String [] errorMessages = validateParam.errorMessages();
        // 如果正则表达式数组为空，则默认为每个属性不进行校验
        if (regexArray.length == 0) {
            regexArray = new String[propertyNames.length];
            for (int i = 0; i < propertyNames.length; i++) {
                regexArray[i] = "";  // 默认不校验
            }
        }

        // 获取方法参数
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            for (int i = 0; i < propertyNames.length; i++) {
                String propertyName = propertyNames[i];
                String regex = regexArray[i];
                String errorMessage = errorMessages[i];
                // 遍历方法参数并校验
                for (Object arg : args) {
                    // 使用反射获取指定属性
                    Field field = getField(arg, propertyName);

                    if (field != null) {
                        field.setAccessible(true);  // 设置私有字段可访问
                        Object value = field.get(arg);  // 获取字段值
                        if (ObjectUtil.isEmpty(value)) {
                            throw new RequestParamException("参数 " + propertyName + " 不能为空");
                        }
                        else if (value != null && !value.toString().isEmpty()) {
                            // 如果提供了正则表达式，进行校验
                            if (!regex.isEmpty() && !Pattern.matches(regex, value.toString())) {
                                throw new RequestParamException("参数 " + propertyName + (errorMessage != null ? errorMessage : " 不符合规定的格式"));
                            }
                        }
                    }
                }
            }
        }
    }

    // 根据字段名称从对象中获取字段
    private Field getField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();  // 向上查找父类
            }
        }
        return null;
    }
}
