package cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 商户子账号 Response VO")
@Data
public class AppMerchantSubAccountRespVO {

    @Schema(description = "子账号 ID", example = "1")
    private Long id;

    @Schema(description = "商户 ID", example = "10")
    private Long merchantId;

    @Schema(description = "子账号用户 ID", example = "100")
    private Long userId;

    @Schema(description = "子账号手机号", example = "13800138000")
    private String mobile;

    @Schema(description = "权限编码列表")
    private List<String> permissionCodes;

    @Schema(description = "服务点 ID 列表")
    private List<Long> servicePointIds;

    @Schema(description = "状态：ENABLE 启用、DISABLE 禁用", example = "ENABLE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
