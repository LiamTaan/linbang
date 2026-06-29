package cn.iocoder.yudao.module.linbang.controller.app.member.security.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 登录记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMemberLoginLogPageReqVO extends PageParam {

    @Schema(description = "是否仅查看成功记录；true 成功，false 失败，为空全部")
    private Boolean success;
}
