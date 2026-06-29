package cn.iocoder.yudao.module.system.controller.app.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 字典数据分组 Response VO")
@Data
public class AppDictDataGroupRespVO {

    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "common_status")
    private String dictType;

    @Schema(description = "字典数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AppDictDataRespVO> dictDataList;

}
