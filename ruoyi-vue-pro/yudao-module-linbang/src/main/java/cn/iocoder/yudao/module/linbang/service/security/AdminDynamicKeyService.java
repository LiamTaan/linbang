package cn.iocoder.yudao.module.linbang.service.security;

import cn.iocoder.yudao.module.linbang.controller.admin.security.vo.AdminDynamicKeyVerifyRespVO;

public interface AdminDynamicKeyService {

    String HEADER_NAME = "X-Linbang-Dynamic-Key-Token";

    AdminDynamicKeyVerifyRespVO verify(String password);

    boolean validateCurrentAdminToken(String token);
}
