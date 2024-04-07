package com.mrnaif.javalab.aop.aspect;

import com.mrnaif.javalab.service.RequestCounterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
class RequestCounterAspect {
  RequestCounterService requestCounterService;

  public RequestCounterAspect(RequestCounterService requestCounterService) {
    this.requestCounterService = requestCounterService;
  }

  @Around(
      "@within(com.mrnaif.javalab.aop.annotation.RequestStats) ||"
          + " @annotation(com.mrnaif.javalab.aop.annotation.RequestStats)")
  public Object incrementRequestStats(ProceedingJoinPoint joinPoint) throws Throwable {
    requestCounterService.increment();
    return joinPoint.proceed();
  }
}
