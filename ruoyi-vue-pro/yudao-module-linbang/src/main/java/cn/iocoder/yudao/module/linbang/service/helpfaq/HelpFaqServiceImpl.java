package cn.iocoder.yudao.module.linbang.service.helpfaq;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.helpfaq.HelpFaqDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.helpfaq.HelpFaqMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.HELP_FAQ_NOT_EXISTS;

@Service
@Validated
public class HelpFaqServiceImpl implements HelpFaqService {

    @Resource
    private HelpFaqMapper helpFaqMapper;

    @Override
    public Long createHelpFaq(HelpFaqSaveReqVO reqVO) {
        HelpFaqDO faq = BeanUtils.toBean(reqVO, HelpFaqDO.class);
        helpFaqMapper.insert(faq);
        return faq.getId();
    }

    @Override
    public void updateHelpFaq(HelpFaqSaveReqVO reqVO) {
        validateHelpFaqExists(reqVO.getId());
        HelpFaqDO faq = BeanUtils.toBean(reqVO, HelpFaqDO.class);
        helpFaqMapper.updateById(faq);
    }

    @Override
    public void deleteHelpFaq(Long id) {
        validateHelpFaqExists(id);
        helpFaqMapper.deleteById(id);
    }

    @Override
    public void deleteHelpFaqList(Collection<Long> ids) {
        helpFaqMapper.deleteByIds(ids);
    }

    @Override
    public HelpFaqRespVO getHelpFaq(Long id) {
        HelpFaqDO faq = validateHelpFaqExists(id);
        return BeanUtils.toBean(faq, HelpFaqRespVO.class);
    }

    @Override
    public PageResult<HelpFaqRespVO> getHelpFaqPage(HelpFaqPageReqVO reqVO) {
        PageResult<HelpFaqDO> page = helpFaqMapper.selectPage(reqVO);
        return BeanUtils.toBean(page, HelpFaqRespVO.class);
    }

    @Override
    public List<AppHelpFaqRespVO> getAppHelpFaqList(AppHelpFaqPageReqVO reqVO) {
        return BeanUtils.toBean(helpFaqMapper.selectAppList(reqVO), AppHelpFaqRespVO.class);
    }

    @Override
    public AppHelpFaqRespVO getAppHelpFaq(Long id) {
        HelpFaqDO faq = validateHelpFaqExists(id);
        if (!"ENABLE".equals(faq.getStatus())) {
            throw exception(HELP_FAQ_NOT_EXISTS);
        }
        return BeanUtils.toBean(faq, AppHelpFaqRespVO.class);
    }

    private HelpFaqDO validateHelpFaqExists(Long id) {
        HelpFaqDO faq = helpFaqMapper.selectById(id);
        if (faq == null) {
            throw exception(HELP_FAQ_NOT_EXISTS);
        }
        return faq;
    }
}
