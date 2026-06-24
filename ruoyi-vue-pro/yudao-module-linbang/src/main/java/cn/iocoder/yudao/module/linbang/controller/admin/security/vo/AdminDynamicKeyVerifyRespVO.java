package cn.iocoder.yudao.module.linbang.controller.admin.security.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminDynamicKeyVerifyRespVO {

    private String verifyToken;

    private LocalDateTime expireTime;
}
