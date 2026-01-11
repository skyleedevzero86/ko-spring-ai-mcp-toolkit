package com.sleekydz86.chat

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Component
@Aspect
class ServiceLogAspect {
    private val log = LoggerFactory.getLogger(ServiceLogAspect::class.java)

    @Around("execution(* com.sleekydz86.chat.domain.application..*.*(..)) || execution(* com.sleekydz86.chat.domain.infrastructure..*.*(..))")
    fun recordTimeLog(joinPoint: ProceedingJoinPoint): Any? {
        val stopWatch = StopWatch()
        stopWatch.start()

        val proceed = joinPoint.proceed()

        val point = "${joinPoint.target.javaClass.name}.${joinPoint.signature.name}"
        stopWatch.stop()

        val takeTime = stopWatch.totalTimeMillis

        when {
            takeTime > 3000 -> log.error("{} 소요 시간이 깁니다 {} 밀리초", point, takeTime)
            takeTime > 2000 -> log.warn("{} 소요 시간 보통 {} 밀리초", point, takeTime)
            else -> log.info("{} 소요 시간 {} 밀리초", point, takeTime)
        }

        return proceed
    }
}
