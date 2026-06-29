package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 推广模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPromoteTemplatePageReqVO extends PageParam {

    @Schema(description = "模板分类", example = "MEETING_NOTICE")
    private String messageCategory;
}
