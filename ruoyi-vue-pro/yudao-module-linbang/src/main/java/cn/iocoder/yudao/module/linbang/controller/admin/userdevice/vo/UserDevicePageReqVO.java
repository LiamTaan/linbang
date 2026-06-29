package cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户设备分页 Request VO")
@Data
public class UserDevicePageReqVO extends PageParam {

    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "设备指纹摘要，支持模糊搜索", example = "f5a1c0d9")
    private String deviceFingerprint;

    @Schema(description = "最近登录 IP，支持模糊搜索", example = "192.168.1.10")
    private String lastIp;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
