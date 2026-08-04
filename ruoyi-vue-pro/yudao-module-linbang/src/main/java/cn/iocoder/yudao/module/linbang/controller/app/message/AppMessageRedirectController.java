package cn.iocoder.yudao.module.linbang.controller.app.message;

import cn.iocoder.yudao.module.linbang.service.app.message.AppMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "用户 App - 消息点击跳转")
@RestController
@RequestMapping("/message/redirect")
@Validated
public class AppMessageRedirectController {

    @Resource
    private AppMessageService appMessageService;

    @GetMapping("/click")
    @PermitAll
    @Operation(summary = "短信/公众号点击跳转并回写点击回执")
    @Parameters({
            @Parameter(name = "recordId", description = "消息记录 ID；缺失或无效时安全跳转到站点根路径"),
            @Parameter(name = "clickToken", description = "消息发送时生成的 32 位随机点击凭证；缺失或无效时安全跳转到站点根路径")
    })
    public void clickRedirect(@RequestParam(value = "recordId", required = false) Long recordId,
                              @RequestParam(value = "clickToken", required = false) String clickToken,
                              HttpServletResponse response) throws IOException {
        response.sendRedirect(appMessageService.recordExternalClickAndResolveTarget(recordId, clickToken));
    }
}
