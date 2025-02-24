package imwhs.eatz_server.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    @Around("execution(* imwhs.eatz_server.service.LikedService.getLikedDetails(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        PerformanceLoggingAspect.log.info(joinPoint.getSignature() + " 실행 시간: " + executionTime + "ms");
        return proceed;
    }
}