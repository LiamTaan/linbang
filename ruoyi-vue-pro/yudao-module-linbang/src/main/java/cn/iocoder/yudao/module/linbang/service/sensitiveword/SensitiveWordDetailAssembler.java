package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.SensitiveWordDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword.SensitiveWordDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class SensitiveWordDetailAssembler {

    private SensitiveWordDetailAssembler() {
    }

    static List<SensitiveWordDetailRespVO.RelatedWordRespVO> buildRelatedWords(List<SensitiveWordDO> relatedWords) {
        if (relatedWords == null || relatedWords.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedWords.stream().map(word -> {
            SensitiveWordDetailRespVO.RelatedWordRespVO respVO = new SensitiveWordDetailRespVO.RelatedWordRespVO();
            respVO.setId(word.getId());
            respVO.setWord(word.getWord());
            respVO.setWordType(word.getWordType());
            respVO.setMatchType(word.getMatchType());
            respVO.setBlockLevel(word.getBlockLevel());
            respVO.setStatus(word.getStatus());
            respVO.setCreateTime(word.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }
}
