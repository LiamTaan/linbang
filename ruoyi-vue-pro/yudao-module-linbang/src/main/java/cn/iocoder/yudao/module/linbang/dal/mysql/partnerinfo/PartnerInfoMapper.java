package cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerInfoMapper extends BaseMapperX<PartnerInfoDO> {

    default PageResult<PartnerInfoDO> selectPage(PartnerInfoPageReqVO reqVO, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PartnerInfoDO>()
                .inIfPresent(PartnerInfoDO::getUserId, matchedUserIds)
                .likeIfPresent(PartnerInfoDO::getPartnerName, reqVO.getPartnerName())
                .eqIfPresent(PartnerInfoDO::getContactMobile, reqVO.getContactMobile())
                .eqIfPresent(PartnerInfoDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PartnerInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PartnerInfoDO::getId));
    }
}
