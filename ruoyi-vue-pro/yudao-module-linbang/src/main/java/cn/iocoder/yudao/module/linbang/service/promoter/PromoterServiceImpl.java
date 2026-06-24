package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_NOT_EXISTS;

@Service
@Validated
public class PromoterServiceImpl implements PromoterService {

    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private CommissionOrderMapper commissionOrderMapper;
    @Resource
    private PromoterRelationMapper promoterRelationMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromoterDO getOrCreatePromoter(Long userId) {
        PromoterDO promoter = promoterMapper.selectByUserId(userId);
        if (promoter != null) {
            return promoter;
        }
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        String inviteCode = "LB" + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        promoter = PromoterDO.builder()
                .userId(userId)
                .levelCode("NORMAL")
                .inviteCode(inviteCode)
                .inviteUrl("/app/promote?code=" + inviteCode)
                .bindUserCount(0)
                .convertCount(0)
                .totalCommissionAmount(BigDecimal.ZERO)
                .availableCommissionAmount(BigDecimal.ZERO)
                .status("ENABLE")
                .build();
        promoterMapper.insert(promoter);
        return promoter;
    }

    @Override
    public PageResult<PromoterRespVO> getPromoterPage(PromoterPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<PromoterDO> pageResult = promoterMapper.selectPage(reqVO, matchedUserIds);
        List<PromoterRespVO> list = BeanUtils.toBean(pageResult.getList(), PromoterRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PromoterDetailRespVO getPromoterDetail(Long id) {
        PromoterDO promoter = promoterMapper.selectById(id);
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        MemberUserDO user = promoter.getUserId() == null ? null : memberUserMapper.selectById(promoter.getUserId());
        List<PromoterRelationDO> relations = promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, id)
                .orderByDesc(PromoterRelationDO::getId));
        List<CommissionOrderDO> commissionOrders = commissionOrderMapper.selectList(new LambdaQueryWrapperX<CommissionOrderDO>()
                .eq(CommissionOrderDO::getPromoterId, id)
                .orderByDesc(CommissionOrderDO::getId));
        Set<Long> relatedUserIds = new HashSet<>();
        relations.forEach(item -> {
            if (item.getUserId() != null) {
                relatedUserIds.add(item.getUserId());
            }
        });
        commissionOrders.forEach(item -> {
            if (item.getUserId() != null) {
                relatedUserIds.add(item.getUserId());
            }
        });
        Set<Long> orderIds = new HashSet<>();
        relations.forEach(item -> {
            if (item.getFirstOrderId() != null) {
                orderIds.add(item.getFirstOrderId());
            }
        });
        commissionOrders.forEach(item -> {
            if (item.getSourceOrderId() != null) {
                orderIds.add(item.getSourceOrderId());
            }
        });
        Set<Long> unitIds = convertSet(commissionOrders, CommissionOrderDO::getSourceUnitId,
                item -> item.getSourceUnitId() != null);
        Map<Long, MemberUserDO> relatedUserMap = relatedUserIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(relatedUserIds), MemberUserDO::getId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        return PromoterDetailAssembler.build(promoter, user, relations, commissionOrders, relatedUserMap, orderMap, unitMap);
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<PromoterRespVO> list) {
        Set<Long> userIds = convertSet(list, PromoterRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }
}
