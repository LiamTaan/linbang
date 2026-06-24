package cn.iocoder.yudao.module.linbang.service.commissionorder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.COMMISSION_ORDER_NOT_EXISTS;

@Service
@Validated
public class CommissionOrderServiceImpl implements CommissionOrderService {

    @Resource
    private CommissionOrderMapper commissionOrderMapper;
    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;

    @Override
    public PageResult<CommissionOrderRespVO> getCommissionOrderPage(CommissionOrderPageReqVO reqVO) {
        List<Long> promoterIds = resolveMatchedPromoterIds(reqVO.getPromoterKeyword());
        if (StrUtil.isNotBlank(reqVO.getPromoterKeyword()) && CollUtil.isEmpty(promoterIds)) {
            return PageResult.empty();
        }
        List<Long> userIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(userIds)) {
            return PageResult.empty();
        }
        PageResult<CommissionOrderDO> pageResult = commissionOrderMapper.selectPage(reqVO, promoterIds, userIds);
        List<CommissionOrderRespVO> list = BeanUtils.toBean(pageResult.getList(), CommissionOrderRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public CommissionOrderDetailRespVO getCommissionOrderDetail(Long id) {
        CommissionOrderDO commissionOrder = commissionOrderMapper.selectById(id);
        if (commissionOrder == null) {
            throw exception(COMMISSION_ORDER_NOT_EXISTS);
        }
        PromoterDO promoter = commissionOrder.getPromoterId() == null ? null : promoterMapper.selectById(commissionOrder.getPromoterId());
        MemberUserDO promoterUser = promoter == null || promoter.getUserId() == null ? null : memberUserMapper.selectById(promoter.getUserId());
        MemberUserDO user = commissionOrder.getUserId() == null ? null : memberUserMapper.selectById(commissionOrder.getUserId());
        OrderInfoDO order = commissionOrder.getSourceOrderId() == null ? null : orderInfoMapper.selectById(commissionOrder.getSourceOrderId());
        OrderUnitDO unit = commissionOrder.getSourceUnitId() == null ? null : orderUnitMapper.selectById(commissionOrder.getSourceUnitId());
        if (order == null && unit != null && unit.getOrderId() != null) {
            order = orderInfoMapper.selectById(unit.getOrderId());
        }
        return CommissionOrderDetailAssembler.build(commissionOrder, promoter, user, promoterUser, order, unit);
    }

    @Override
    public PageResult<CommissionOrderDO> getAppCommissionOrderPage(Long promoterId, AppCommissionPageReqVO reqVO) {
        return commissionOrderMapper.selectAppPage(promoterId, reqVO);
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private List<Long> resolveMatchedPromoterIds(String promoterKeyword) {
        List<Long> matchedUserIds = resolveMatchedUserIds(promoterKeyword);
        if (matchedUserIds == null) {
            return null;
        }
        if (CollUtil.isEmpty(matchedUserIds)) {
            return matchedUserIds;
        }
        return convertList(promoterMapper.selectList(new LambdaQueryWrapperX<PromoterDO>()
                .in(PromoterDO::getUserId, matchedUserIds)), PromoterDO::getId);
    }

    private void fillUserDisplayInfo(List<CommissionOrderRespVO> list) {
        Set<Long> promoterIds = convertSet(list, CommissionOrderRespVO::getPromoterId,
                item -> item.getPromoterId() != null);
        Map<Long, PromoterDO> promoterMap = promoterIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(promoterMapper.selectBatchIds(promoterIds), PromoterDO::getId);
        Set<Long> orderIds = convertSet(list, CommissionOrderRespVO::getSourceOrderId,
                item -> item.getSourceOrderId() != null);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> unitIds = convertSet(list, CommissionOrderRespVO::getSourceUnitId,
                item -> item.getSourceUnitId() != null);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Set<Long> userIds = new HashSet<>(convertSet(list, CommissionOrderRespVO::getUserId,
                item -> item.getUserId() != null));
        promoterMap.values().forEach(item -> {
            if (item.getUserId() != null) {
                userIds.add(item.getUserId());
            }
        });
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            PromoterDO promoter = promoterMap.get(item.getPromoterId());
            if (promoter == null) {
                return;
            }
            MemberUserDO promoterUser = userMap.get(promoter.getUserId());
            if (promoterUser == null) {
                return;
            }
            item.setPromoterUserNo(promoterUser.getUserNo());
            item.setPromoterUserNickname(promoterUser.getNickname());
            item.setPromoterUserMobile(promoterUser.getMobile());
            OrderInfoDO order = orderMap.get(item.getSourceOrderId());
            if (order != null) {
                item.setSourceOrderNo(order.getOrderNo());
            }
            OrderUnitDO unit = unitMap.get(item.getSourceUnitId());
            if (unit != null) {
                item.setSourceUnitNo(unit.getUnitNo());
            }
        });
    }
}
