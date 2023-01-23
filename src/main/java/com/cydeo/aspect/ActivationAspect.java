package com.cydeo.aspect;

import com.cydeo.dto.UserDto;
import com.cydeo.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ActivationAspect {


    private final SecurityService securityService;

    public ActivationAspect(SecurityService securityService) {
        this.securityService = securityService;
    }
    private UserDto getUser(){
     return  securityService.getLoggedInUser();
    }

    @Pointcut("@annotation(com.cydeo.annotation.Activation)")
    public void companyActivation() {
    }

    @AfterReturning(pointcut="companyActivation()", returning="results")
    public void beforeCompanyActivation(JoinPoint joinPoint, Object results) {

        log.info("{} {},  with username: {} from  {} , accessed {} for {} "
               ,getUser().getFirstname()
                ,getUser().getLastname()
                ,getUser().getUsername()
                ,getUser().getCompany()
                ,joinPoint.getSignature().getName()
                ,results.toString());
    }
}


