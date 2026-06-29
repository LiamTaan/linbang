package cn.iocoder.yudao.module.linbang.dal.mysql.sensitiveword;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword.SensitiveWordDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.*;

/**
 * 敏感词表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapperX<SensitiveWordDO> {

    default PageResult<SensitiveWordDO> selectPage(SensitiveWordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SensitiveWordDO>()
                .likeIfPresent(SensitiveWordDO::getWord, reqVO.getWord())
                .eqIfPresent(SensitiveWordDO::getWordType, reqVO.getWordType())
                .eqIfPresent(SensitiveWordDO::getMatchType, reqVO.getMatchType())
                .eqIfPresent(SensitiveWordDO::getBlockLevel, reqVO.getBlockLevel())
                .likeIfPresent(SensitiveWordDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(SensitiveWordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SensitiveWordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SensitiveWordDO::getId));
    }

}
