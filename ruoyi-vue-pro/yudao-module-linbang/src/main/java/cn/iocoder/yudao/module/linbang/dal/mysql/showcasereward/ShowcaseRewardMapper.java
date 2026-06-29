package cn.iocoder.yudao.module.linbang.dal.mysql.showcasereward;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ShowcaseRewardMapper extends BaseMapperX<ShowcaseRewardDO> {

    default ShowcaseRewardDO selectActiveByMerchantId(Long merchantId, LocalDateTime now) {
        return selectOne(new LambdaQueryWrapperX<ShowcaseRewardDO>()
                .eq(ShowcaseRewardDO::getMerchantId, merchantId)
                .eq(ShowcaseRewardDO::getAuditStatus, "APPROVED")
                .eq(ShowcaseRewardDO::getPriorityEnabled, true)
                .le(ShowcaseRewardDO::getEffectiveStartTime, now)
                .ge(ShowcaseRewardDO::getEffectiveEndTime, now)
                .orderByDesc(ShowcaseRewardDO::getId)
                .last("LIMIT 1"));
    }

    default List<ShowcaseRewardDO> selectListByMerchantId(Long merchantId) {
        return selectList(new LambdaQueryWrapperX<ShowcaseRewardDO>()
                .eq(ShowcaseRewardDO::getMerchantId, merchantId)
                .orderByDesc(ShowcaseRewardDO::getId));
    }

    default PageResult<ShowcaseRewardDO> selectPage(cn.iocoder.yudao.framework.common.pojo.PageParam pageParam,
                                                    Long merchantId, String auditStatus) {
        return selectPage(pageParam, new LambdaQueryWrapperX<ShowcaseRewardDO>()
                .eqIfPresent(ShowcaseRewardDO::getMerchantId, merchantId)
                .eqIfPresent(ShowcaseRewardDO::getAuditStatus, auditStatus)
                .orderByDesc(ShowcaseRewardDO::getId));
    }
}
