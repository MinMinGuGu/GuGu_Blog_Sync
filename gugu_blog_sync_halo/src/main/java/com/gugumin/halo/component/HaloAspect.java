package com.gugumin.halo.component;

import com.dtflys.forest.Forest;
import com.gugumin.halo.config.HaloConfig;
import com.gugumin.halo.pojo.request.AccountRequest;
import com.gugumin.halo.service.IHaloApi;
import com.jayway.jsonpath.JsonPath;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * The type Halo aspect.
 *
 * @author minmin
 * @date 2023 /03/08
 */
@Aspect
@Component
public class HaloAspect {
    private final HaloConfig haloConfig;
    @Resource
    private IHaloApi haloApi;

    /**
     * Instantiates a new Halo aspect.
     *
     * @param haloConfig the halo config
     */
    public HaloAspect(HaloConfig haloConfig) {
        this.haloConfig = haloConfig;
    }

    /**
     * Halo site.
     */
    @Pointcut("execution(* com.gugumin.halo.service.impl.HaloSiteImpl.*(..))")
    public void haloSite() {
    }

    /**
     * Auth.
     */
    @Before("haloSite()")
    public void auth() {
        String responseBody = haloApi.login(new AccountRequest(haloConfig.getUsername(), haloConfig.getPassword()));
        String token = JsonPath.read(responseBody, "$.data.access_token").toString();
        Forest.config().setVariableValue("token", token);
    }
}
