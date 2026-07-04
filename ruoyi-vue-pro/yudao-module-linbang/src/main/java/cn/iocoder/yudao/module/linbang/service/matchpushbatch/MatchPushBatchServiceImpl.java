package cn.iocoder.yudao.module.linbang.service.matchpushbatch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.MatchPushBatchRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch.MatchPushBatchDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.matchpushbatch.MatchPushBatchMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Service
@Validated
public class MatchPushBatchServiceImpl implements MatchPushBatchService {

    @Resource
    private MatchPushBatchMapper matchPushBatchMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;

    @Override
    public PageResult<MatchPushBatchRespVO> getMatchPushBatchPage(MatchPushBatchPageReqVO reqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(reqVO);
        if (shouldReturnEmpty(reqVO.getOrderNo(), matchedOrderIds) || shouldReturnEmpty(reqVO.getUserKeyword(), matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(reqVO.getUnitNo());
        if (shouldReturnEmpty(reqVO.getUnitNo(), matchedUnitIds)) {
            return PageResult.empty();
        }
        PageResult<MatchPushBatchDO> pageResult = matchPushBatchMapper.selectPage(reqVO, matchedOrderIds, matchedUnitIds);
        List<MatchPushBatchRespVO> list = BeanUtils.toBean(pageResult.getList(), MatchPushBatchRespVO.class);
        fillDisplayInfo(list, pageResult.getList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public MatchPushBatchRespVO getMatchPushBatch(Long id) {
        MatchPushBatchDO batch = matchPushBatchMapper.selectById(id);
        if (batch == null) {
            return null;
        }
        MatchPushBatchRespVO respVO = BeanUtils.toBean(batch, MatchPushBatchRespVO.class);
        fillDisplayInfo(Collections.singletonList(respVO), Collections.singletonList(batch));
        return respVO;
    }

    private List<Long> resolveMatchedOrderIds(MatchPushBatchPageReqVO reqVO) {
        Set<Long> orderIds = null;
        if (StrUtil.isNotBlank(reqVO.getOrderNo())) {
            orderIds = new LinkedHashSet<>(convertList(orderInfoMapper.selectListByOrderNo(reqVO.getOrderNo()), OrderInfoDO::getId));
        }
        if (StrUtil.isNotBlank(reqVO.getUserKeyword())) {
            List<MemberUserDO> users = memberUserMapper.selectListByKeyword(reqVO.getUserKeyword());
            if (CollUtil.isEmpty(users)) {
                return Collections.emptyList();
            }
            Set<Long> userIds = convertSet(users, MemberUserDO::getId);
            List<OrderInfoDO> orders = orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                    .in(OrderInfoDO::getUserId, userIds)
                    .orderByDesc(OrderInfoDO::getId));
            Set<Long> userOrderIds = convertSet(orders, OrderInfoDO::getId);
            orderIds = mergeMatchedIds(orderIds, userOrderIds);
        }
        return orderIds == null ? null : new java.util.ArrayList<>(orderIds);
    }

    private List<Long> resolveMatchedUnitIds(String unitNo) {
        if (StrUtil.isBlank(unitNo)) {
            return null;
        }
        return convertList(orderUnitMapper.selectListByUnitNo(unitNo), OrderUnitDO::getId);
    }

    private boolean shouldReturnEmpty(String keyword, Collection<Long> matchedIds) {
        return StrUtil.isNotBlank(keyword) && CollUtil.isEmpty(matchedIds);
    }

    private Set<Long> mergeMatchedIds(Set<Long> left, Set<Long> right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        left.retainAll(right);
        return left;
    }

    private void fillDisplayInfo(List<MatchPushBatchRespVO> list, List<MatchPushBatchDO> sourceList) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> orderIds = convertSet(sourceList, MatchPushBatchDO::getOrderId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> userIds = convertSet(orderMap.values(), OrderInfoDO::getUserId);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectBatchIds(userIds), MemberUserDO::getId);
        Set<Long> unitIds = convertSet(sourceList, MatchPushBatchDO::getUnitId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Set<Integer> batchNos = convertSet(sourceList, MatchPushBatchDO::getPushBatchNo);
        List<OrderMatchRecordDO> matchRecords = unitIds.isEmpty() || batchNos.isEmpty() ? Collections.emptyList()
                : orderMatchRecordMapper.selectList(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .in(OrderMatchRecordDO::getUnitId, unitIds)
                .in(OrderMatchRecordDO::getPushBatchNo, batchNos)
                .orderByAsc(OrderMatchRecordDO::getId));
        Map<String, List<OrderMatchRecordDO>> matchRecordMap = matchRecords.stream()
                .collect(Collectors.groupingBy(this::buildBatchKey));
        Set<Long> merchantIds = new LinkedHashSet<>();
        merchantIds.addAll(convertSet(matchRecords, OrderMatchRecordDO::getMerchantId));
        merchantIds.addAll(convertSet(unitMap.values(), OrderUnitDO::getMerchantId));
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);

        for (int i = 0; i < list.size(); i++) {
            MatchPushBatchRespVO item = list.get(i);
            MatchPushBatchDO source = sourceList.get(i);
            fillOrderInfo(item, orderMap.get(source.getOrderId()), userMap);
            OrderUnitDO unit = unitMap.get(source.getUnitId());
            fillUnitInfo(item, unit);
            fillAcceptedMerchantInfo(item, unit, merchantMap);
            fillPushedMerchantInfo(item, source, matchRecordMap, merchantMap);
        }
    }

    private void fillOrderInfo(MatchPushBatchRespVO item, OrderInfoDO order, Map<Long, MemberUserDO> userMap) {
        if (order == null) {
            return;
        }
        item.setOrderNo(order.getOrderNo());
        item.setOrderStatus(order.getStatus());
        item.setUserId(order.getUserId());
        MemberUserDO user = userMap.get(order.getUserId());
        if (user != null) {
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        }
    }

    private void fillUnitInfo(MatchPushBatchRespVO item, OrderUnitDO unit) {
        if (unit == null) {
            return;
        }
        item.setUnitNo(unit.getUnitNo());
        item.setUnitSeq(unit.getUnitSeq());
        item.setUnitTitle(unit.getUnitTitle());
        item.setUnitStatus(unit.getStatus());
    }

    private void fillAcceptedMerchantInfo(MatchPushBatchRespVO item, OrderUnitDO unit, Map<Long, MerchantInfoDO> merchantMap) {
        if (unit == null || unit.getMerchantId() == null) {
            return;
        }
        item.setAcceptedMerchantId(unit.getMerchantId());
        MerchantInfoDO merchant = merchantMap.get(unit.getMerchantId());
        if (merchant == null) {
            return;
        }
        item.setAcceptedMerchantName(merchant.getMerchantName());
        item.setAcceptedMerchantContactName(merchant.getContactName());
        item.setAcceptedMerchantContactMobile(merchant.getContactMobile());
    }

    private void fillPushedMerchantInfo(MatchPushBatchRespVO item, MatchPushBatchDO batch,
                                        Map<String, List<OrderMatchRecordDO>> matchRecordMap,
                                        Map<Long, MerchantInfoDO> merchantMap) {
        List<OrderMatchRecordDO> matchRecords = matchRecordMap.getOrDefault(buildBatchKey(batch), Collections.emptyList());
        item.setPushedMerchantCount(matchRecords.size());
        item.setAcceptedMatchCount((int) matchRecords.stream()
                .filter(record -> StrUtil.equalsIgnoreCase(record.getStatus(), "ACCEPTED"))
                .count());
        LinkedHashSet<String> merchantNames = matchRecords.stream()
                .map(OrderMatchRecordDO::getMerchantId)
                .filter(Objects::nonNull)
                .map(merchantMap::get)
                .filter(Objects::nonNull)
                .map(MerchantInfoDO::getMerchantName)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        item.setPushedMerchantNames(StrUtil.join("、", merchantNames));
    }

    private String buildBatchKey(MatchPushBatchDO batch) {
        return buildBatchKey(batch.getUnitId(), batch.getPushBatchNo(), batch.getStageNo());
    }

    private String buildBatchKey(OrderMatchRecordDO record) {
        return buildBatchKey(record.getUnitId(), record.getPushBatchNo(), record.getStageNo());
    }

    private String buildBatchKey(Long unitId, Integer pushBatchNo, Integer stageNo) {
        return unitId + ":" + pushBatchNo + ":" + stageNo;
    }
}
