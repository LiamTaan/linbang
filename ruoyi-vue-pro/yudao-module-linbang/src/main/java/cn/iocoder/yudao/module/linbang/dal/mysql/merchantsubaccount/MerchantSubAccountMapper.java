package cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccount;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccount.MerchantSubAccountDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MerchantSubAccountMapper extends BaseMapperX<MerchantSubAccountDO> {

    default MerchantSubAccountDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<MerchantSubAccountDO>()
                .eq(MerchantSubAccountDO::getUserId, userId)
                .last("LIMIT 1"));
    }

    default List<MerchantSubAccountDO> selectListByMerchantId(Long merchantId) {
        return selectList(new LambdaQueryWrapperX<MerchantSubAccountDO>()
                .eq(MerchantSubAccountDO::getMerchantId, merchantId)
                .orderByDesc(MerchantSubAccountDO::getId));
    }

    default PageResult<MerchantSubAccountDO> selectPageByMerchantId(cn.iocoder.yudao.framework.common.pojo.PageParam pageParam, Long merchantId) {
        return selectPage(pageParam, new LambdaQueryWrapperX<MerchantSubAccountDO>()
                .eq(MerchantSubAccountDO::getMerchantId, merchantId)
                .orderByDesc(MerchantSubAccountDO::getId));
    }

    default PageResult<MerchantSubAccountDO> selectPage(MerchantSubAccountPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MerchantSubAccountDO>()
                .eqIfPresent(MerchantSubAccountDO::getMerchantId, reqVO.getMerchantId())
                .inIfPresent(MerchantSubAccountDO::getUserId, userIds)
                .eqIfPresent(MerchantSubAccountDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MerchantSubAccountDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MerchantSubAccountDO::getId));
    }
}
