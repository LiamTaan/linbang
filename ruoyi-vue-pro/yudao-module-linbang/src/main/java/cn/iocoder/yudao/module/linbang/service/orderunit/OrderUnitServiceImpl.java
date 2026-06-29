package cn.iocoder.yudao.module.linbang.service.orderunit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo.OrderUnitDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof.OrderUnitProofDO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunitproof.OrderUnitProofMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 拆分单元 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class OrderUnitServiceImpl implements OrderUnitService {

    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderAcceptRecordMapper orderAcceptRecordMapper;
    @Resource
    private OrderUnitProofMapper orderUnitProofMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private FileService fileService;

    @Override
    public Long createOrderUnit(OrderUnitSaveReqVO createReqVO) {
        // 插入
        OrderUnitDO orderUnit = BeanUtils.toBean(createReqVO, OrderUnitDO.class);
        orderUnitMapper.insert(orderUnit);

        // 返回
        return orderUnit.getId();
    }

    @Override
    public void updateOrderUnit(OrderUnitSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderUnitExists(updateReqVO.getId());
        // 更新
        OrderUnitDO updateObj = BeanUtils.toBean(updateReqVO, OrderUnitDO.class);
        orderUnitMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderUnit(Long id) {
        // 校验存在
        validateOrderUnitExists(id);
        // 删除
        orderUnitMapper.deleteById(id);
    }

    @Override
        public void deleteOrderUnitListByIds(List<Long> ids) {
        // 删除
        orderUnitMapper.deleteByIds(ids);
        }


    private void validateOrderUnitExists(Long id) {
        if (orderUnitMapper.selectById(id) == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
    }

    @Override
    public OrderUnitDO getOrderUnit(Long id) {
        return orderUnitMapper.selectById(id);
    }

    @Override
    public OrderUnitDetailRespVO getOrderUnitDetail(Long id) {
        OrderUnitDO unit = orderUnitMapper.selectById(id);
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        OrderUnitDO prevUnit = unit.getPrevUnitId() == null ? null : orderUnitMapper.selectById(unit.getPrevUnitId());
        MemberUserDO orderUser = order.getUserId() == null ? null : memberUserMapper.selectById(order.getUserId());
        MerchantInfoDO merchant = unit.getMerchantId() == null ? null : merchantInfoMapper.selectById(unit.getMerchantId());
        List<OrderUnitProofDO> proofs = orderUnitProofMapper.selectList(new LambdaQueryWrapperX<OrderUnitProofDO>()
                .eq(OrderUnitProofDO::getUnitId, unit.getId())
                .orderByDesc(OrderUnitProofDO::getProofTime, OrderUnitProofDO::getId));
        List<OrderAcceptRecordDO> acceptRecords = orderAcceptRecordMapper.selectList(new LambdaQueryWrapperX<OrderAcceptRecordDO>()
                .eq(OrderAcceptRecordDO::getUnitId, unit.getId())
                .orderByDesc(OrderAcceptRecordDO::getAcceptTime, OrderAcceptRecordDO::getId));
        List<ComplaintDO> complaints = complaintMapper.selectList(new LambdaQueryWrapperX<ComplaintDO>()
                .eq(ComplaintDO::getUnitId, unit.getId())
                .orderByDesc(ComplaintDO::getCreateTime, ComplaintDO::getId));
        List<AppealDO> appeals = appealMapper.selectList(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getUnitId, unit.getId())
                .orderByDesc(AppealDO::getCreateTime, AppealDO::getId));
        List<OrderOperateLogDO> operateLogs = orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(OrderOperateLogDO::getUnitId, unit.getId())
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));
        Set<Long> relatedMerchantIds = new HashSet<>();
        if (unit.getMerchantId() != null) {
            relatedMerchantIds.add(unit.getMerchantId());
        }
        proofs.stream().map(OrderUnitProofDO::getMerchantId).filter(Objects::nonNull).forEach(relatedMerchantIds::add);
        acceptRecords.stream().map(OrderAcceptRecordDO::getMerchantId).filter(Objects::nonNull).forEach(relatedMerchantIds::add);
        Map<Long, MerchantInfoDO> merchantMap = relatedMerchantIds.isEmpty() ? Collections.emptyMap() : convertMap(
                merchantInfoMapper.selectBatchIds(relatedMerchantIds),
                MerchantInfoDO::getId);
        Map<Long, FileDO> fileMap = buildFileMap(proofs.stream().map(OrderUnitProofDO::getFileId).collect(Collectors.toSet()));

        OrderUnitDetailRespVO respVO = BeanUtils.toBean(unit, OrderUnitDetailRespVO.class);
        respVO.setOrderId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setOrderTitle(order.getRequireDesc());
        respVO.setOrderStatus(order.getStatus());
        respVO.setUserId(order.getUserId());
        if (orderUser != null) {
            respVO.setUserNo(orderUser.getUserNo());
            respVO.setUserNickname(orderUser.getNickname());
            respVO.setUserMobile(orderUser.getMobile());
        }
        respVO.setMerchantId(unit.getMerchantId());
        fillMerchantSummary(respVO, merchant);
        respVO.setPrevUnitNo(prevUnit != null ? prevUnit.getUnitNo() : null);
        respVO.setVerifyStatus(unit.getVerifyStatus());
        respVO.setVerifyCode(unit.getVerifyCode());
        respVO.setVerifyTime(unit.getVerifyTime());
        respVO.setVerifyBy(unit.getVerifyBy());
        respVO.setVerifyRemark(unit.getVerifyRemark());
        respVO.setProofs(proofs.stream().map(proof -> {
            OrderUnitDetailRespVO.OrderUnitProofRespVO proofResp = new OrderUnitDetailRespVO.OrderUnitProofRespVO();
            proofResp.setId(proof.getId());
            proofResp.setFileId(proof.getFileId());
            proofResp.setFileUrl(Optional.ofNullable(fileMap.get(proof.getFileId())).map(FileDO::getUrl).orElse(proof.getFileUrl()));
            proofResp.setFileHash(proof.getFileHash());
            proofResp.setMerchantId(proof.getMerchantId());
            fillMerchantSummary(proofResp, merchantMap.get(proof.getMerchantId()));
            proofResp.setProofType(proof.getProofType());
            proofResp.setProofDesc(proof.getProofDesc());
            proofResp.setProofTime(proof.getProofTime());
            proofResp.setDeviceTime(proof.getDeviceTime());
            proofResp.setLongitude(proof.getLongitude());
            proofResp.setLatitude(proof.getLatitude());
            proofResp.setAddressText(proof.getAddressText());
            return proofResp;
        }).collect(Collectors.toList()));
        respVO.setAcceptRecords(acceptRecords.stream().map(record -> {
            OrderUnitDetailRespVO.OrderAcceptRecordRespVO recordResp = new OrderUnitDetailRespVO.OrderAcceptRecordRespVO();
            recordResp.setId(record.getId());
            recordResp.setMerchantId(record.getMerchantId());
            fillMerchantSummary(recordResp, merchantMap.get(record.getMerchantId()));
            recordResp.setAcceptTime(record.getAcceptTime());
            recordResp.setDistanceKm(record.getDistanceKm());
            recordResp.setAcceptResult(record.getAcceptResult());
            recordResp.setRemark(record.getRemark());
            return recordResp;
        }).collect(Collectors.toList()));
        respVO.setComplaints(complaints.stream().map(complaint -> {
            OrderUnitDetailRespVO.OrderComplaintRespVO complaintResp = new OrderUnitDetailRespVO.OrderComplaintRespVO();
            complaintResp.setId(complaint.getId());
            complaintResp.setComplaintNo(complaint.getComplaintNo());
            complaintResp.setComplainantUserId(complaint.getComplainantUserId());
            complaintResp.setRespondentUserId(complaint.getRespondentUserId());
            complaintResp.setComplaintType(complaint.getComplaintType());
            complaintResp.setContent(complaint.getContent());
            complaintResp.setStatus(complaint.getStatus());
            complaintResp.setResultDesc(complaint.getResultDesc());
            complaintResp.setHandleTime(complaint.getHandleTime());
            complaintResp.setCreateTime(complaint.getCreateTime());
            return complaintResp;
        }).collect(Collectors.toList()));
        respVO.setAppeals(appeals.stream().map(appeal -> {
            OrderUnitDetailRespVO.OrderAppealRespVO appealResp = new OrderUnitDetailRespVO.OrderAppealRespVO();
            appealResp.setId(appeal.getId());
            appealResp.setAppealNo(appeal.getAppealNo());
            appealResp.setUserId(appeal.getUserId());
            appealResp.setAppealType(appeal.getAppealType());
            appealResp.setContent(appeal.getContent());
            appealResp.setStatus(appeal.getStatus());
            appealResp.setAuditStatus(appeal.getAuditStatus());
            appealResp.setAuditRemark(appeal.getAuditRemark());
            appealResp.setRejectReason(appeal.getRejectReason());
            appealResp.setAuditTime(appeal.getAuditTime());
            appealResp.setAppealExpireTime(appeal.getAppealExpireTime());
            appealResp.setCreateTime(appeal.getCreateTime());
            return appealResp;
        }).collect(Collectors.toList()));
        respVO.setOperateLogs(operateLogs.stream().map(log -> {
            OrderUnitDetailRespVO.OrderOperateLogRespVO logResp = new OrderUnitDetailRespVO.OrderOperateLogRespVO();
            logResp.setId(log.getId());
            logResp.setOperateType(log.getOperateType());
            logResp.setOperateRole(log.getOperateRole());
            logResp.setOperateBy(log.getOperateBy());
            logResp.setBeforeStatus(log.getBeforeStatus());
            logResp.setAfterStatus(log.getAfterStatus());
            logResp.setRemark(log.getRemark());
            logResp.setOperateTime(log.getOperateTime());
            return logResp;
        }).collect(Collectors.toList()));
        respVO.setTimeline(buildTimeline(order, unit, proofs, complaints, appeals, operateLogs));
        return respVO;
    }

    @Override
    public void unlockOrderUnit(OrderUnitUnlockReqVO reqVO) {
        OrderUnitDO orderUnit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (orderUnit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderUnitDO updateObj = new OrderUnitDO();
        updateObj.setId(orderUnit.getId());
        updateObj.setIsLocked(Boolean.FALSE);
        updateObj.setLockReason(reqVO.getUnlockRemark());
        orderUnitMapper.updateById(updateObj);
        orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                .orderId(orderUnit.getOrderId())
                .unitId(orderUnit.getId())
                .operateType("ADMIN_UNLOCK_UNIT")
                .operateRole("ADMIN")
                .operateBy(null)
                .beforeStatus(orderUnit.getStatus())
                .afterStatus(orderUnit.getStatus())
                .remark(StrUtil.blankToDefault(reqVO.getUnlockRemark(), "管理端人工解锁"))
                .operateTime(java.time.LocalDateTime.now())
                .build());
    }

    @Override
    public PageResult<OrderUnitRespVO> getOrderUnitPage(OrderUnitPageReqVO pageReqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO);
        if (hasOrderQuery(pageReqVO) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedPrevUnitIds = resolveMatchedPrevUnitIds(pageReqVO);
        if (hasPrevUnitQuery(pageReqVO) && CollUtil.isEmpty(matchedPrevUnitIds)) {
            return PageResult.empty();
        }
        PageResult<OrderUnitDO> pageResult = orderUnitMapper.selectPage(pageReqVO, matchedOrderIds, matchedPrevUnitIds);
        List<OrderUnitRespVO> list = BeanUtils.toBean(pageResult.getList(), OrderUnitRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedOrderIds(OrderUnitPageReqVO pageReqVO) {
        if (pageReqVO.getOrderId() != null) {
            return Collections.singletonList(pageReqVO.getOrderId());
        }
        if (StrUtil.isBlank(pageReqVO.getOrderNo())) {
            return null;
        }
        return convertList(orderInfoMapper.selectListByOrderNo(pageReqVO.getOrderNo()), OrderInfoDO::getId);
    }

    private List<Long> resolveMatchedPrevUnitIds(OrderUnitPageReqVO pageReqVO) {
        if (pageReqVO.getPrevUnitId() != null) {
            return Collections.singletonList(pageReqVO.getPrevUnitId());
        }
        if (StrUtil.isBlank(pageReqVO.getPrevUnitNo())) {
            return null;
        }
        return convertList(orderUnitMapper.selectListByUnitNo(pageReqVO.getPrevUnitNo()), OrderUnitDO::getId);
    }

    private boolean hasOrderQuery(OrderUnitPageReqVO pageReqVO) {
        return pageReqVO.getOrderId() != null || StrUtil.isNotBlank(pageReqVO.getOrderNo());
    }

    private boolean hasPrevUnitQuery(OrderUnitPageReqVO pageReqVO) {
        return pageReqVO.getPrevUnitId() != null || StrUtil.isNotBlank(pageReqVO.getPrevUnitNo());
    }

    private void fillDisplayInfo(List<OrderUnitRespVO> list) {
        Set<Long> orderIds = convertSet(list, OrderUnitRespVO::getOrderId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> prevUnitIds = convertSet(list, OrderUnitRespVO::getPrevUnitId);
        Map<Long, OrderUnitDO> prevUnitMap = prevUnitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(prevUnitIds), OrderUnitDO::getId);
        Set<Long> merchantIds = convertSet(list, OrderUnitRespVO::getMerchantId);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        list.forEach(item -> {
            OrderInfoDO order = orderMap.get(item.getOrderId());
            if (order != null) {
                item.setOrderNo(order.getOrderNo());
            }
            OrderUnitDO prevUnit = prevUnitMap.get(item.getPrevUnitId());
            if (prevUnit != null) {
                item.setPrevUnitNo(prevUnit.getUnitNo());
            }
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
                item.setMerchantContactName(merchant.getContactName());
                item.setMerchantContactMobile(merchant.getContactMobile());
            }
        });
    }

    private Map<Long, FileDO> buildFileMap(Set<Long> fileIds) {
        Map<Long, FileDO> fileMap = new HashMap<>();
        for (Long fileId : fileIds) {
            if (fileId == null || fileMap.containsKey(fileId)) {
                continue;
            }
            try {
                fileMap.put(fileId, fileService.getFile(fileId));
            } catch (Exception ignored) {
            }
        }
        return fileMap;
    }

    private void fillMerchantSummary(OrderUnitDetailRespVO respVO, MerchantInfoDO merchant) {
        if (merchant == null) {
            return;
        }
        respVO.setMerchantName(merchant.getMerchantName());
        respVO.setMerchantContactName(merchant.getContactName());
        respVO.setMerchantContactMobile(merchant.getContactMobile());
    }

    private void fillMerchantSummary(OrderUnitDetailRespVO.OrderUnitProofRespVO respVO, MerchantInfoDO merchant) {
        if (merchant == null) {
            return;
        }
        respVO.setMerchantName(merchant.getMerchantName());
        respVO.setMerchantContactName(merchant.getContactName());
        respVO.setMerchantContactMobile(merchant.getContactMobile());
    }

    private void fillMerchantSummary(OrderUnitDetailRespVO.OrderAcceptRecordRespVO respVO, MerchantInfoDO merchant) {
        if (merchant == null) {
            return;
        }
        respVO.setMerchantName(merchant.getMerchantName());
        respVO.setMerchantContactName(merchant.getContactName());
        respVO.setMerchantContactMobile(merchant.getContactMobile());
    }

    private List<OrderUnitDetailRespVO.OrderTimelineRespVO> buildTimeline(OrderInfoDO order,
                                                                          OrderUnitDO unit,
                                                                          List<OrderUnitProofDO> proofs,
                                                                          List<ComplaintDO> complaints,
                                                                          List<AppealDO> appeals,
                                                                          List<OrderOperateLogDO> operateLogs) {
        List<OrderUnitDetailRespVO.OrderTimelineRespVO> timeline = new ArrayList<>();
        timeline.add(buildTimelineItem("ORDER", order.getId(), "主订单创建", order.getRequireDesc(), order.getStatus(), order.getCreateTime()));
        timeline.add(buildTimelineItem("UNIT", unit.getId(), "单元创建", unit.getUnitTitle(), unit.getStatus(), unit.getCreateTime()));
        if (unit.getVerifyTime() != null) {
            timeline.add(buildTimelineItem("VERIFY", unit.getId(), "单元核销", unit.getVerifyRemark(), unit.getVerifyStatus(), unit.getVerifyTime()));
        }
        if (unit.getFinishTime() != null) {
            timeline.add(buildTimelineItem("UNIT", unit.getId(), "单元完成", unit.getUnitTitle(), unit.getStatus(), unit.getFinishTime()));
        }
        for (OrderUnitProofDO proof : proofs) {
            timeline.add(buildTimelineItem("PROOF", proof.getId(), "完工凭证上传", proof.getProofDesc(), proof.getProofType(), proof.getProofTime()));
        }
        for (ComplaintDO complaint : complaints) {
            timeline.add(buildTimelineItem("COMPLAINT", complaint.getId(), "投诉提交", complaint.getContent(), complaint.getStatus(), complaint.getCreateTime()));
        }
        for (AppealDO appeal : appeals) {
            timeline.add(buildTimelineItem("APPEAL", appeal.getId(), "申诉提交", appeal.getContent(), appeal.getStatus(), appeal.getCreateTime()));
        }
        for (OrderOperateLogDO log : operateLogs) {
            timeline.add(buildTimelineItem("LOG", log.getId(), log.getOperateType(), log.getRemark(), log.getAfterStatus(), log.getOperateTime()));
        }
        timeline.sort(Comparator.comparing(OrderUnitDetailRespVO.OrderTimelineRespVO::getEventTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return timeline;
    }

    private OrderUnitDetailRespVO.OrderTimelineRespVO buildTimelineItem(String type, Long bizId, String title,
                                                                        String content, String status,
                                                                        LocalDateTime eventTime) {
        OrderUnitDetailRespVO.OrderTimelineRespVO item = new OrderUnitDetailRespVO.OrderTimelineRespVO();
        item.setTimelineType(type);
        item.setBizId(bizId);
        item.setTitle(title);
        item.setContent(content);
        item.setStatus(status);
        item.setEventTime(eventTime);
        return item;
    }

}
