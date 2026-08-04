package cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 推广员 分页查询 Request VO")
public class PromoterPageReqVO extends PageParam {

    @Schema(description = "用户筛选关键词，可匹配用户编号、昵称或手机号")
    private String userKeyword;

    @Schema(description = "推广邀请码")
    private String inviteCode;

    @Schema(description = "推广员状态筛选：ENABLE 启用、DISABLE 停用")
    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
