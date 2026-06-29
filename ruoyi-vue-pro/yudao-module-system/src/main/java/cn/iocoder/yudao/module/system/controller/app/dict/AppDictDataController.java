package cn.iocoder.yudao.module.system.controller.app.dict;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.app.dict.vo.AppDictDataGroupRespVO;
import cn.iocoder.yudao.module.system.controller.app.dict.vo.AppDictDataRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class AppDictDataController {

    @Resource
    private DictDataService dictDataService;

    @GetMapping("/type")
    @Operation(summary = "根据字典类型查询字典数据信息")
    @Parameter(name = "type", description = "字典类型", required = true, example = "common_status")
    @PermitAll
    public CommonResult<List<AppDictDataRespVO>> getDictDataListByType(@RequestParam("type") String type) {
        List<DictDataDO> list = dictDataService.getDictDataList(
                CommonStatusEnum.ENABLE.getStatus(), type);
        return success(BeanUtils.toBean(list, AppDictDataRespVO.class));
    }

    @GetMapping("/types")
    @Operation(summary = "根据多个字典类型批量查询字典数据信息")
    @Parameter(name = "types", description = "字典类型列表，支持多次传参或逗号分隔", required = true,
            example = "common_status,lb_order_status")
    @PermitAll
    public CommonResult<List<AppDictDataGroupRespVO>> getDictDataGroupListByTypes(@RequestParam("types") List<String> types) {
        List<String> normalizedTypes = convertList(types, String::trim);
        List<DictDataDO> list = dictDataService.getDictDataList(CommonStatusEnum.ENABLE.getStatus(), null);
        Map<String, List<DictDataDO>> dictDataMap = CollectionUtils.convertMultiMap(list,
                DictDataDO::getDictType, dictData -> dictData);
        return success(convertList(normalizedTypes, type -> {
            AppDictDataGroupRespVO respVO = new AppDictDataGroupRespVO();
            respVO.setDictType(type);
            respVO.setDictDataList(BeanUtils.toBean(dictDataMap.getOrDefault(type, Collections.emptyList()),
                    AppDictDataRespVO.class));
            return respVO;
        }));
    }

    @GetMapping("/type-and-value")
    @Operation(summary = "根据字典类型和字典值查询单条字典数据")
    @Parameter(name = "type", description = "字典类型", required = true, example = "common_status")
    @Parameter(name = "value", description = "字典值", required = true, example = "0")
    @PermitAll
    public CommonResult<AppDictDataRespVO> getDictDataByTypeAndValue(@RequestParam("type") String type,
                                                                      @RequestParam("value") String value) {
        DictDataDO dictData = dictDataService.getDictData(type, value);
        if (dictData == null || !Objects.equals(dictData.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            return success(null);
        }
        return success(BeanUtils.toBean(dictData, AppDictDataRespVO.class));
    }

}
