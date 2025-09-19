package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerLoggingAspect {

    private final LogService logService;

    @Before("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public void logBeforeSaveManager(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            AuthUser authUser = (AuthUser) args[0];
            Long todoId = (Long) args[1];
            ManagerSaveRequest request = (ManagerSaveRequest) args[2];

            logService.saveLog(authUser.getId(), todoId, request.getManagerUserId());

        } catch (Exception e) {
            log.error("매니저 등록 전 로그 저장 실패", e);
        }
    }
}