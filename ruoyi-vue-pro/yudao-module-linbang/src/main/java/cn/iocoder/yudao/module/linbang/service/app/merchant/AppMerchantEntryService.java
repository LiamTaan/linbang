package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryRespVO;

import javax.validation.Valid;

public interface AppMerchantEntryService {

    Long createEntry(Long authUserId, @Valid AppMerchantEntryCreateReqVO reqVO);

    AppMerchantEntryRespVO getCurrentEntry(Long authUserId);

}
