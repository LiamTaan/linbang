package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 平台配置 分页查询 Request VO")
public class PlatformConfigPageReqVO extends PageParam {

    @Schema(description = "平台配置分类编码，用于管理端按业务域分组展示")
    private String category;

    @Schema(description = "配置项名称")
    private String name;

    @Schema(description = "平台配置键；同一租户内唯一")
    private String key;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
