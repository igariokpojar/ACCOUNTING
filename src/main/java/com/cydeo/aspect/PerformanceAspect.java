package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {


    private String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Pointcut("@annotation(com.cydeo.annotation.ExecutionTime)")
    public void executionTimePC() {}

    @Around("executionTimePC()")
    public Object aroundAnyExecutionTimeAdvice(ProceedingJoinPoint proceedingJoinPoint) {

        long beforeTime = System.currentTimeMillis();
        Object result = null;
        log.info("Execution starts:");

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        long afterTime = System.currentTimeMillis();

        log.info("Time taken to execute: {} ms - Method: {}"
                , (afterTime - beforeTime), proceedingJoinPoint.getSignature().toShortString());

        return result;

    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void afterReturningGetMappingAnnotation() {}

        @AfterThrowing(pointcut = "afterReturningGetMappingAnnotation()", throwing = "exception")
    public void afterThrowingGetMappingOperation(JoinPoint joinPoint, RuntimeException exception) {
        log.error("After Throwing -> Method: {}, Exception: {}"
                , joinPoint.getSignature().toShortString()
                , exception.getMessage());
    }

}
