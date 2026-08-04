package cn.iocoder.yudao.module.linbang.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 合作商可处理的纠纷类型。
 */
@Getter
@AllArgsConstructor
public enum PartnerDisputeTypeEnum implements ArrayValuable<String> {

    COMPLAINT("COMPLAINT", "投诉"),
    APPEAL("APPEAL", "申诉");

    public static final String[] ARRAYS = Arrays.stream(values())
            .map(PartnerDisputeTypeEnum::getCode)
            .toArray(String[]::new);

    private final String code;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static PartnerDisputeTypeEnum getByCode(String code) {
        return ArrayUtil.firstMatch(item -> item.getCode().equalsIgnoreCase(code), values());
    }
}
