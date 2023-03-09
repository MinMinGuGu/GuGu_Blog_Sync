package com.gugumin.halo.component;

import com.dtflys.forest.Forest;
import com.gugumin.halo.config.HaloConfig;
import com.gugumin.halo.pojo.request.Account;
import com.gugumin.halo.service.IHaloApi;
import com.jayway.jsonpath.JsonPath;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author minmin
 * @date 2023/03/08
 */
@Aspect
@Component
public class HaloAspect {
    private final HaloConfig haloConfig;
    @Resource
    private IHaloApi haloApi;

    public HaloAspect(HaloConfig haloConfig) {
        this.haloConfig = haloConfig;
    }

    @Pointcut("execution(* com.gugumin.halo.service.impl.HaloSiteImpl.*(..))")
    public void haloSite() {
    }

    @Before("haloSite()")
    public void auth() {
        String responseBody = haloApi.login(new Account(haloConfig.getUsername(), haloConfig.getPassword()));
        String token = JsonPath.read(responseBody, "$.data.access_token").toString();
        Forest.config().setVariableValue("token", token);
    }
}
