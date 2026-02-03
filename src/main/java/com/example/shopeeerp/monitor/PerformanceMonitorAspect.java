package com.example.shopeeerp.monitor;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceMonitorAspect {

    @Value("${app.monitor.slow-ms:1000}")
    private long slowThreshold;

    @Around("execution(* com.example.shopeeerp.controller..*.*(..))")
    public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();
        boolean success = false;
        try {
            Object result = pjp.proceed();
            success = true;
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - start;
            log.error("接口异常: method={}, duration={}ms", method, duration, e);
            throw e;
        } finally {
            if (success) {
                long duration = System.currentTimeMillis() - start;
                if (duration >= slowThreshold) {
                    log.warn("慢接口: method={}, duration={}ms", method, duration);
                } else {
                    log.info("接口调用: method={}, duration={}ms", method, duration);
                }
            }
        }
    }
}
