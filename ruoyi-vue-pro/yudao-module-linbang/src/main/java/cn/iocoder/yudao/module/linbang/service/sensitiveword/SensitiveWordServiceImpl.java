package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.SensitiveWordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.SensitiveWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.SensitiveWordSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword.SensitiveWordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.sensitiveword.SensitiveWordMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.SENSITIVE_WORD_NOT_EXISTS;

/**
 * 敏感词表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Resource
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public Long createSensitiveWord(SensitiveWordSaveReqVO createReqVO) {
        // 插入
        SensitiveWordDO sensitiveWord = BeanUtils.toBean(createReqVO, SensitiveWordDO.class);
        sensitiveWordMapper.insert(sensitiveWord);

        // 返回
        return sensitiveWord.getId();
    }

    @Override
    public void updateSensitiveWord(SensitiveWordSaveReqVO updateReqVO) {
        // 校验存在
        validateSensitiveWordExists(updateReqVO.getId());
        // 更新
        SensitiveWordDO updateObj = BeanUtils.toBean(updateReqVO, SensitiveWordDO.class);
        sensitiveWordMapper.updateById(updateObj);
    }

    @Override
    public void deleteSensitiveWord(Long id) {
        // 校验存在
        validateSensitiveWordExists(id);
        // 删除
        sensitiveWordMapper.deleteById(id);
    }

    @Override
        public void deleteSensitiveWordListByIds(List<Long> ids) {
        // 删除
        sensitiveWordMapper.deleteByIds(ids);
        }


    private void validateSensitiveWordExists(Long id) {
        if (sensitiveWordMapper.selectById(id) == null) {
            throw exception(SENSITIVE_WORD_NOT_EXISTS);
        }
    }

    @Override
    public SensitiveWordDO getSensitiveWord(Long id) {
        return sensitiveWordMapper.selectById(id);
    }

    @Override
    public SensitiveWordDetailRespVO getSensitiveWordDetail(Long id) {
        SensitiveWordDO sensitiveWord = sensitiveWordMapper.selectById(id);
        if (sensitiveWord == null) {
            throw exception(SENSITIVE_WORD_NOT_EXISTS);
        }

        List<SensitiveWordDO> sameWordTypeWords = sensitiveWordMapper.selectList(new LambdaQueryWrapperX<SensitiveWordDO>()
                .eq(SensitiveWordDO::getWordType, sensitiveWord.getWordType())
                .ne(SensitiveWordDO::getId, sensitiveWord.getId())
                .orderByDesc(SensitiveWordDO::getStatus)
                .orderByDesc(SensitiveWordDO::getId));
        List<SensitiveWordDO> relatedWords = sameWordTypeWords.size() > 10 ? sameWordTypeWords.subList(0, 10) : sameWordTypeWords;

        List<SensitiveWordDO> allWords = sensitiveWordMapper.selectList(new LambdaQueryWrapperX<SensitiveWordDO>()
                .orderByDesc(SensitiveWordDO::getId));
        int sameMatchTypeCount = 0;
        int sameBlockLevelCount = 0;
        for (SensitiveWordDO word : allWords) {
            if (sensitiveWord.getMatchType() != null && sensitiveWord.getMatchType().equals(word.getMatchType())) {
                sameMatchTypeCount++;
            }
            if (sensitiveWord.getBlockLevel() != null && sensitiveWord.getBlockLevel().equals(word.getBlockLevel())) {
                sameBlockLevelCount++;
            }
        }

        SensitiveWordDetailRespVO respVO = BeanUtils.toBean(sensitiveWord, SensitiveWordDetailRespVO.class);
        respVO.setSameWordTypeCount(sameWordTypeWords.size() + 1);
        respVO.setSameMatchTypeCount(sameMatchTypeCount);
        respVO.setSameBlockLevelCount(sameBlockLevelCount);
        respVO.setRelatedWords(sameWordTypeWords.isEmpty()
                ? Collections.emptyList()
                : SensitiveWordDetailAssembler.buildRelatedWords(relatedWords));
        return respVO;
    }

    @Override
    public PageResult<SensitiveWordDO> getSensitiveWordPage(SensitiveWordPageReqVO pageReqVO) {
        return sensitiveWordMapper.selectPage(pageReqVO);
    }

}
