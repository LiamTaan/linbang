package cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 商户子账号分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantSubAccountPageReqVO extends PageParam {

    @Schema(description = "商户 ID", example = "1")
    private Long merchantId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "13800138000")
    private String userKeyword;

    @Schema(description = "状态：ENABLE 启用、DISABLE 禁用", example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] createTime;
}
