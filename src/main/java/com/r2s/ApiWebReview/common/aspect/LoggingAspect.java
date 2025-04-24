package com.r2s.ApiWebReview.common.aspect;

import com.r2s.ApiWebReview.common.annotation.LogExecutionTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(com.r2s.ApiWebReview.common.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object res = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("⏱ {} executed in {} ms", joinPoint.getSignature(), (end - start));

        return res;
    }
}
