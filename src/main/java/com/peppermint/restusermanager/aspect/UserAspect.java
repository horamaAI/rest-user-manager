package com.peppermint.restusermanager.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
@Component
public class UserAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // seems to not work
    // @Before("execution(* com.peppermint.restusermanager.UserService.*(..))")
    // public void logBefore(JoinPoint joinPoint) {
    // logger.info("Calling method: {}", joinPoint.getSignature().getName());
    // }


    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();
        logger.info("Execution time for {}: {} ms", joinPoint.getSignature().getName(),
                (System.currentTimeMillis() - startTime));

        return proceed;
    }
}
