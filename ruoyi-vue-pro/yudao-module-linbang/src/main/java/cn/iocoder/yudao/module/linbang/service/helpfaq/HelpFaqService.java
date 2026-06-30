package cn.iocoder.yudao.module.linbang.service.helpfaq;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqRespVO;

import java.util.Collection;
import java.util.List;

public interface HelpFaqService {

    Long createHelpFaq(HelpFaqSaveReqVO reqVO);

    void updateHelpFaq(HelpFaqSaveReqVO reqVO);

    void deleteHelpFaq(Long id);

    void deleteHelpFaqList(Collection<Long> ids);

    HelpFaqRespVO getHelpFaq(Long id);

    PageResult<HelpFaqRespVO> getHelpFaqPage(HelpFaqPageReqVO reqVO);

    List<AppHelpFaqRespVO> getAppHelpFaqList(AppHelpFaqPageReqVO reqVO);

    AppHelpFaqRespVO getAppHelpFaq(Long id);
}
