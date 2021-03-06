package com.softjourn.common.spring.aspects.logging;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Aspect to log every call to any public method
 * in all beans in package that matches nex pattern .*\.service\.*
 * Aspect will log on DEBUG level
 */
@Aspect
@Order(value = 101)
@Slf4j
@Component
public class CallsLoggingAspect {

    @Around(value = "execution(public * *..service.*.*(..))")
    public Object logCall(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            return log(joinPoint);
        } else {
            return joinPoint.proceed();
        }
    }

    private Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.debug(prepareBeforeLogMessage(joinPoint));
            Object retVal = joinPoint.proceed();
            log.debug(prepareAfterReturnLogMessage(retVal, joinPoint));
            return retVal;
        } catch (Throwable t) {
            log.debug(prepareAfterThrowing(joinPoint, t));
            throw t;
        }
    }

    private String prepareAfterThrowing(ProceedingJoinPoint joinPoint, Throwable t) {
        return "Execution of method " + getCallingMethod(joinPoint) + " failed with exception " + t.getMessage();
    }

    private String prepareAfterReturnLogMessage(Object retVal, ProceedingJoinPoint joinPoint) {
        String callingMethod = getCallingMethod(joinPoint);
        return "Return value of method " + callingMethod + " is " + retVal;
    }

    private String prepareBeforeLogMessage(ProceedingJoinPoint joinPoint) {
        String callingMethod = getCallingMethod(joinPoint);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String argsString = matchNamesWithArgs(paramNames, args);
        return "Call of method " + callingMethod + " with args " + argsString;
    }

    String matchNamesWithArgs(String[] paramNames, Object[] args) {
        if (paramNames == null || paramNames.length == 0) {
            if (args == null) return "[]";
            return Arrays.toString(args);
        } else if (paramNames.length != args.length) {
            //should never happens
            throw new IllegalStateException("Param names count " + paramNames.length + " don't matches actual args count " + args.length);
        } else {
            return IntStream.range(0, args.length)
                    .mapToObj(i -> paramNames[i] + "=" + args[i])
                    .collect(Collectors.joining(", ", "[", "]"));
        }
    }

    private String getCallingMethod(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Class calledClass = signature.getDeclaringType();
        String method = signature.getName();
        return calledClass.getSimpleName() + "." + method + "()";
    }
}
