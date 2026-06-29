package cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 商户子账号 Response VO")
@Data
public class MerchantSubAccountRespVO {

    @Schema(description = "子账号 ID", example = "1")
    private Long id;

    @Schema(description = "商户 ID", example = "1")
    private Long merchantId;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "子账号用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "用户编号")
    private String userNo;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "权限编码列表")
    private List<String> permissionCodes;

    @Schema(description = "服务点 ID 列表")
    private List<Long> servicePointIds;

    @Schema(description = "服务点名称列表")
    private List<String> servicePointNames;

    @Schema(description = "状态：ENABLE 启用、DISABLE 禁用")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
