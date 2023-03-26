package com.gugumin.core.components;

import com.gugumin.core.config.CoreConfig;
import com.gugumin.core.utils.SystemProxyUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * The type Proxy aspect.
 *
 * @author minmin
 * @date 2023 /03/09
 */
@Component
@Aspect
public class ProxyAspect {
    private final CoreConfig coreConfig;

    /**
     * Instantiates a new Proxy aspect.
     *
     * @param coreConfig the config
     */
    public ProxyAspect(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    /**
     * Git.
     */
    @Pointcut("execution(* com.gugumin.core.service.impl.GitServiceImpl.*(..))")
    public void git() {
    }

    /**
     * Proxy object.
     *
     * @param proceedingJoinPoint the proceeding join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("git()")
    public Object proxy(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        CoreConfig.Proxy configProxy = coreConfig.getProxy();
        if (Boolean.TRUE.equals(configProxy.getOpen())) {
            SystemProxyUtil.setHttpProxy(configProxy.getHost(), configProxy.getPort(), configProxy.getVersion(), configProxy.getUsername(), configProxy.getPassword());
            Object proceed = proceedingJoinPoint.proceed();
            SystemProxyUtil.removeHttpProxy();
            return proceed;
        }
        return proceedingJoinPoint.proceed();
    }
}
